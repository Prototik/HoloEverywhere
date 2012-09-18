package com.WazaBe.HoloEverywhere.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Checkable;

import com.WazaBe.HoloEverywhere.sherlock.SBase;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ListView extends android.widget.ListView {
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

	class OnItemLongClickListenerWrapper implements OnItemLongClickListener {
		private OnItemLongClickListener wrapped;

		@Override
		public boolean onItemLongClick(AdapterView<?> view, View child,
				int position, long id) {
			boolean handled = doLongPress(child, position, id);
			if (!handled && wrapped != null) {
				return wrapped.onItemLongClick(view, child, position, id);
			}
			return true;
		}

		public void setWrapped(OnItemLongClickListener listener) {
			wrapped = listener;
		}
	}

	public static final int CHOICE_MODE_MULTIPLE_MODAL = AbsListView.CHOICE_MODE_MULTIPLE_MODAL;
	private ActionMode actionMode;
	private int checkedItemCount;
	private int choiceMode;
	private MultiChoiceModeWrapper choiceModeListener;
	private OnItemLongClickListenerWrapper longClickListenerWrapper;

	private SBase sBase;

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

	protected boolean doLongPress(final View child,
			final int longPressPosition, final long longPressId) {
		if (choiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
			if (actionMode == null
					&& (actionMode = startActionMode(choiceModeListener)) != null) {
				setItemChecked(longPressPosition, true);
			}
			return true;
		}
		return false;
	}

	@Override
	public int getChoiceMode() {
		return choiceMode;
	}

	public final SBase getSBase() {
		return sBase;
	}

	protected void init(Context context) {
		setOnLongClickListener(null);
		if (context instanceof SBase) {
			sBase = (SBase) context;
		}
	}

	@Override
	public boolean performItemClick(View view, int position, long id) {
		boolean handled = false;
		boolean dispatchItemClick = true;
		boolean checkStateChanged = false;
		if (choiceMode != CHOICE_MODE_NONE) {
			handled = true;
			if (choiceMode == CHOICE_MODE_MULTIPLE
					|| choiceMode == CHOICE_MODE_MULTIPLE_MODAL
					&& actionMode != null) {
				boolean newValue = !getCheckedItemPositions().get(position);
				setItemChecked(position, newValue);
				if (actionMode != null) {
					choiceModeListener.onItemCheckedStateChanged(actionMode,
							position, id, newValue);
					dispatchItemClick = false;
				}
				checkStateChanged = true;
				return false;
			} else if (choiceMode == CHOICE_MODE_SINGLE) {
				boolean newValue = !getCheckedItemPositions().get(position);
				setItemChecked(position, newValue);
				checkStateChanged = true;
			}
			if (checkStateChanged) {
				updateOnScreenCheckedViews();
			}
		}
		if (dispatchItemClick) {
			handled |= super.performItemClick(view, position, id);
		}
		return handled;
	}

	@Override
	public void setChoiceMode(int choiceMode) {
		this.choiceMode = choiceMode;
		if (actionMode != null) {
			actionMode.finish();
			actionMode = null;
		}
		if (choiceMode == CHOICE_MODE_MULTIPLE_MODAL && VERSION.SDK_INT < 11) {
			choiceMode = CHOICE_MODE_MULTIPLE;
			clearChoices();
			checkedItemCount = 0;
			setLongClickable(true);
			updateOnScreenCheckedViews();
			requestLayout();
			invalidate();
		}
		super.setChoiceMode(choiceMode);
	}

	@Override
	public void setItemChecked(int position, boolean value) {
		if (choiceMode == CHOICE_MODE_NONE) {
			return;
		}
		SparseBooleanArray checkStates = getCheckedItemPositions();
		if (value && choiceMode == CHOICE_MODE_MULTIPLE_MODAL
				&& actionMode == null) {
			actionMode = startActionMode(choiceModeListener);
		}
		if (choiceMode == CHOICE_MODE_MULTIPLE
				|| choiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
			checkStates.put(position, value);
			if (value) {
				checkedItemCount++;
			} else {
				checkedItemCount--;
			}
			if (actionMode != null) {
				final long id = getAdapter().getItemId(position);
				choiceModeListener.onItemCheckedStateChanged(actionMode,
						position, id, value);
			}
		} else {
			if (value || isItemChecked(position)) {
				checkStates.clear();
			}
			if (value) {
				checkStates.put(position, true);
			}
		}
		requestLayout();
		invalidate();
	}

	public void setMultiChoiceModeListener(MultiChoiceModeListener listener) {
		if (choiceModeListener == null) {
			choiceModeListener = new MultiChoiceModeWrapper();
		}
		choiceModeListener.setWrapped(listener);
	}

	@Override
	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		if (longClickListenerWrapper == null) {
			longClickListenerWrapper = new OnItemLongClickListenerWrapper();
		}
		longClickListenerWrapper.setWrapped(listener);
		super.setOnItemLongClickListener(longClickListenerWrapper);
	}

	public final void setSBase(SBase sBase) {
		this.sBase = sBase;
	}

	public ActionMode startActionMode(ActionMode.Callback callback) {
		if (actionMode != null) {
			return actionMode;
		}
		if (sBase != null) {
			actionMode = sBase.startActionMode(callback);
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
