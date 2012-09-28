package com.WazaBe.HoloEverywhere.app;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.widget.LinearLayout;

public class FragmentBreadCrumbs extends ViewGroup implements
		FragmentManager.OnBackStackChangedListener {
	public interface OnBreadCrumbClickListener {
		public boolean onBreadCrumbClick(BackStackEntry backStack, int flags);
	}

	FragmentActivity mActivity;
	LinearLayout mContainer;
	LayoutInflater mInflater;
	int mMaxVisible = -1;
	private OnBreadCrumbClickListener mOnBreadCrumbClickListener;

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getTag() instanceof BackStackEntry) {
				BackStackEntry bse = (BackStackEntry) v.getTag();
				if (bse == mParentEntry) {
					if (mParentClickListener != null) {
						mParentClickListener.onClick(v);
					}
				} else {
					if (mOnBreadCrumbClickListener != null) {
						if (mOnBreadCrumbClickListener.onBreadCrumbClick(
								bse == mTopEntry ? null : bse, 0)) {
							return;
						}
					}
					if (bse == mTopEntry) {
						mActivity.getSupportFragmentManager().popBackStack();
					} else {
						mActivity.getSupportFragmentManager().popBackStack(
								bse.getId(), 0);
					}
				}
			}
		}
	};

	private OnClickListener mParentClickListener;
	BackStackEntry mParentEntry;
	BackStackEntry mTopEntry;

	public FragmentBreadCrumbs(Context context) {
		this(context, null);
	}

	public FragmentBreadCrumbs(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.style.Widget_FragmentBreadCrumbs);
	}

	public FragmentBreadCrumbs(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private BackStackEntry createBackStackEntry(final CharSequence title,
			final CharSequence shortTitle) {
		if (title == null) {
			return null;
		}
		return new BackStackEntry() {
			@Override
			public CharSequence getBreadCrumbShortTitle() {
				return shortTitle;
			}

			@Override
			public int getBreadCrumbShortTitleRes() {
				return 0;
			}

			@Override
			public CharSequence getBreadCrumbTitle() {
				return title;
			}

			@Override
			public int getBreadCrumbTitleRes() {
				return 0;
			}

			@Override
			public int getId() {
				return 2837452;
			}

			@Override
			public String getName() {
				return "backstackentry";
			}
		};
	}

	private BackStackEntry getPreEntry(int index) {
		if (mParentEntry != null) {
			return index == 0 ? mParentEntry : mTopEntry;
		} else {
			return mTopEntry;
		}
	}

	private int getPreEntryCount() {
		return (mTopEntry != null ? 1 : 0) + (mParentEntry != null ? 1 : 0);
	}

	@Override
	public void onBackStackChanged() {
		updateCrumbs();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = getChildAt(i);
			int childRight = getPaddingLeft() + child.getMeasuredWidth()
					- getPaddingRight();
			int childBottom = getPaddingTop() + child.getMeasuredHeight()
					- getPaddingBottom();
			child.layout(getPaddingLeft(), getPaddingTop(), childRight,
					childBottom);
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int count = getChildCount();
		int maxHeight = 0;
		int maxWidth = 0;
		int measuredChildState = 0;
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				measureChild(child, widthMeasureSpec, heightMeasureSpec);
				maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
				maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
				if (VERSION.SDK_INT >= 11) {
					measuredChildState = combineMeasuredStates(
							measuredChildState, child.getMeasuredState());
				} else {
					measuredChildState = combineMeasuredStates(
							measuredChildState, 0);
				}
			}
		}
		maxWidth += getPaddingLeft() + getPaddingRight();
		maxHeight += getPaddingTop() + getPaddingBottom();
		maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
		maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
		setMeasuredDimension(
				resolveSizeAndState(maxWidth, widthMeasureSpec,
						measuredChildState),
				resolveSizeAndState(maxHeight, heightMeasureSpec,
						measuredChildState << MEASURED_HEIGHT_STATE_SHIFT));
	}

	@SuppressLint("NewApi")
	public void setActivity(FragmentActivity a) {
		mActivity = a;
		mInflater = (LayoutInflater) a
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContainer = (LinearLayout) mInflater.inflate(
				R.layout.fragment_bread_crumbs, this, false);
		addView(mContainer);
		a.getSupportFragmentManager().addOnBackStackChangedListener(this);
		updateCrumbs();
		if (VERSION.SDK_INT >= 11) {
			setLayoutTransition(new LayoutTransition());
		}
	}

	public void setMaxVisible(int visibleCrumbs) {
		if (visibleCrumbs < 1) {
			throw new IllegalArgumentException(
					"visibleCrumbs must be greater than zero");
		}
		mMaxVisible = visibleCrumbs;
	}

	public void setOnBreadCrumbClickListener(OnBreadCrumbClickListener listener) {
		mOnBreadCrumbClickListener = listener;
	}

	public void setParentTitle(CharSequence title, CharSequence shortTitle,
			OnClickListener listener) {
		mParentEntry = createBackStackEntry(title, shortTitle);
		mParentClickListener = listener;
		updateCrumbs();
	}

	public void setTitle(CharSequence title, CharSequence shortTitle) {
		mTopEntry = createBackStackEntry(title, shortTitle);
		updateCrumbs();
	}

	void updateCrumbs() {
		FragmentManager fm = mActivity.getSupportFragmentManager();
		int numEntries = fm.getBackStackEntryCount();
		int numPreEntries = getPreEntryCount();
		int numViews = mContainer.getChildCount();
		for (int i = 0; i < numEntries + numPreEntries; i++) {
			BackStackEntry bse = i < numPreEntries ? getPreEntry(i) : fm
					.getBackStackEntryAt(i - numPreEntries);
			if (i < numViews) {
				View v = mContainer.getChildAt(i);
				Object tag = v.getTag();
				if (tag != bse) {
					for (int j = i; j < numViews; j++) {
						mContainer.removeViewAt(i);
					}
					numViews = i;
				}
			}
			if (i >= numViews) {
				final View item = mInflater.inflate(
						R.layout.fragment_bread_crumb_item, this, false);
				final TextView text = (TextView) item.findViewById(R.id.title);
				text.setText(bse.getBreadCrumbTitle());
				text.setTag(bse);
				if (i == 0) {
					item.findViewById(R.id.left_icon).setVisibility(View.GONE);
				}
				mContainer.addView(item);
				text.setOnClickListener(mOnClickListener);
			}
		}
		int viewI = numEntries + numPreEntries;
		numViews = mContainer.getChildCount();
		while (numViews > viewI) {
			mContainer.removeViewAt(numViews - 1);
			numViews--;
		}
		for (int i = 0; i < numViews; i++) {
			final View child = mContainer.getChildAt(i);
			child.findViewById(R.id.title).setEnabled(i < numViews - 1);
			if (mMaxVisible > 0) {
				child.setVisibility(i < numViews - mMaxVisible ? View.GONE
						: View.VISIBLE);
				final View leftIcon = child.findViewById(R.id.left_icon);
				leftIcon.setVisibility(i > numViews - mMaxVisible && i != 0 ? View.VISIBLE
						: View.GONE);
			}
		}
	}
}