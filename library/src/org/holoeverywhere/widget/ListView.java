
package org.holoeverywhere.widget;

import org.holoeverywhere.IHoloActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ListAdapter;

import com.actionbarsherlock.internal.view.menu.ContextMenuBuilder.ContextMenuInfoGetter;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ListView extends android.widget.ListView implements
        ContextMenuInfoGetter {
    public interface MultiChoiceModeListener extends ActionMode.Callback {
        public void onItemCheckedStateChanged(ActionMode mode, int position,
                long id, boolean checked);
    }

    protected class MultiChoiceModeWrapper implements MultiChoiceModeListener {
        protected MultiChoiceModeListener wrapped;

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (wrapped == null) {
                return false;
            }
            return wrapped.onActionItemClicked(mode, item);
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (wrapped == null) {
                return false;
            }
            if (wrapped.onCreateActionMode(mode, menu)) {
                setLongClickable(false);
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (wrapped == null) {
                return;
            }
            wrapped.onDestroyActionMode(mode);
            actionMode = null;
            clearChoices();
            checkedItemCount = 0;
            updateOnScreenCheckedViews();
            invalidateViews();
            setLongClickable(true);
            requestLayout();
            invalidate();
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position,
                long id, boolean checked) {
            if (wrapped == null) {
                return;
            }
            wrapped.onItemCheckedStateChanged(mode, position, id, checked);
            if (checkedItemCount == 0) {
                mode.finish();
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (wrapped == null) {
                return false;
            }
            return wrapped.onPrepareActionMode(mode, menu);
        }

        public void setWrapped(MultiChoiceModeListener wrapped) {
            this.wrapped = wrapped;
        }
    }

    private class OnItemLongClickListenerWrapper implements
            OnItemLongClickListener {
        protected OnItemLongClickListener wrapped;

        @Override
        public boolean onItemLongClick(AdapterView<?> view, View child,
                int position, long id) {
            boolean handled = doLongPress(child, position, id);
            if (!handled) {
                if (wrapped != null) {
                    if (wrapped.onItemLongClick(view, child, position, id)) {
                        return true;
                    }
                }
                contextMenuInfo = createContextMenuInfo(view, position, id);
                handled = ListView.super.showContextMenuForChild(ListView.this);
                if (handled) {
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    return true;
                }
            }
            return false;
        }
    }

    public static final int CHOICE_MODE_MULTIPLE_MODAL = AbsListView.CHOICE_MODE_MULTIPLE_MODAL;
    private ActionMode actionMode;
    private int checkedItemCount;
    private int choiceMode;
    private final MultiChoiceModeWrapper choiceModeListener = new MultiChoiceModeWrapper();
    private ContextMenuInfo contextMenuInfo;
    private IHoloActivity holoActivity;
    private final OnItemLongClickListenerWrapper longClickListenerWrapper = new OnItemLongClickListenerWrapper();

    public ListView(Context context) {
        super(context);
        init(context);
    }

    public ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    protected ContextMenuInfo createContextMenuInfo(View view, int position,
            long id) {
        return new AdapterContextMenuInfo(view, position, id);
    }

    protected boolean doLongPress(final View child,
            final int longPressPosition, final long longPressId) {
        if (choiceMode == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
            if (actionMode == null
                    && (actionMode = startActionMode(choiceModeListener)) != null) {
                setItemChecked(longPressPosition, true);
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
            return true;
        }
        return false;
    }

    public final IHoloActivity getBase() {
        return holoActivity;
    }

    @Override
    public int getChoiceMode() {
        return choiceMode;
    }

    @Override
    public ContextMenuInfo getContextMenuInfo() {
        return contextMenuInfo;
    }

    protected void init(Context context) {
        super.setOnItemLongClickListener(longClickListenerWrapper);
        if (context instanceof IHoloActivity) {
            holoActivity = (IHoloActivity) context;
        }
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        if (choiceMode == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
            boolean newValue = !getCheckedItemPositions().get(position);
            setItemChecked(position, newValue);
            if (actionMode != null) {
                choiceModeListener.onItemCheckedStateChanged(actionMode,
                        position, id, newValue);
            }
            return true;
        }
        return super.performItemClick(view, position, id);
    }

    public final void setBase(IHoloActivity mBase) {
        holoActivity = mBase;
    }

    @Override
    public void setChoiceMode(int choiceMode) {
        if (this.choiceMode == choiceMode) {
            return;
        }
        this.choiceMode = choiceMode;
        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }
        if (choiceMode == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
            super.setOnItemLongClickListener(longClickListenerWrapper);
            clearChoices();
            checkedItemCount = 0;
            setLongClickable(true);
            updateOnScreenCheckedViews();
            requestLayout();
            invalidate();
            super.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        } else {
            super.setChoiceMode(choiceMode);
        }
    }

    @Override
    public void setItemChecked(int position, boolean value) {
        if (choiceMode == ListView.CHOICE_MODE_MULTIPLE_MODAL) {
            if (value && actionMode == null) {
                actionMode = startActionMode(choiceModeListener);
            }
            super.setItemChecked(position, value);
            checkedItemCount += value ? 1 : -1;
            if (actionMode != null) {
                ListAdapter adapter = getAdapter();
                if (adapter != null) {
                    choiceModeListener.onItemCheckedStateChanged(actionMode,
                            position, adapter.getItemId(position), value);
                }
            }
            requestLayout();
            invalidate();
        } else {
            super.setItemChecked(position, value);
        }
    }

    public void setMultiChoiceModeListener(MultiChoiceModeListener listener) {
        choiceModeListener.setWrapped(listener);
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        longClickListenerWrapper.wrapped = listener;
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        if (actionMode != null) {
            return actionMode;
        }
        if (holoActivity != null) {
            actionMode = holoActivity.startActionMode(callback);
        } else {
            throw new RuntimeException(
                    "ListView must have SBase (setSBase(SBase))");
        }
        return actionMode;
    }

    @SuppressLint("NewApi")
    private void updateOnScreenCheckedViews() {
        final int firstPos = getFirstVisiblePosition();
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int position = firstPos + i;
            boolean state = getCheckedItemPositions().get(position);
            if (child instanceof Checkable) {
                ((Checkable) child).setChecked(state);
            } else if (VERSION.SDK_INT >= 11) {
                child.setActivated(state);
            }
        }
    }
}
