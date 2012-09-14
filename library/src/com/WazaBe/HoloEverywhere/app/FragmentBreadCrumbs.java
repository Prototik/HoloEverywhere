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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.R;

public class FragmentBreadCrumbs extends ViewGroup implements
		FragmentManager.OnBackStackChangedListener {
	/**
	 * Interface to intercept clicks on the bread crumbs.
	 */
	public interface OnBreadCrumbClickListener {
		/**
		 * Called when a bread crumb is clicked.
		 * 
		 * @param backStack
		 *            The BackStackEntry whose bread crumb was clicked. May be
		 *            null, if this bread crumb is for the root of the back
		 *            stack.
		 * @param flags
		 *            Additional information about the entry. Currently always
		 *            0.
		 * 
		 * @return Return true to consume this click. Return to false to allow
		 *         the default action (popping back stack to this entry) to
		 *         occur.
		 */
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

	/** Listener to inform when a parent entry is clicked */
	private OnClickListener mParentClickListener;

	BackStackEntry mParentEntry;

	// Hahah
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
				// random id
				return 2837452;
			}

			@Override
			public String getName() {
				return "backstackentry";
			}
		};
	}

	/**
	 * Returns the pre-entry corresponding to the index. If there is a parent
	 * and a top entry set, parent has an index of zero and top entry has an
	 * index of 1. Returns null if the specified index doesn't exist or is null.
	 * 
	 * @param index
	 *            should not be more than {@link #getPreEntryCount()} - 1
	 */
	private BackStackEntry getPreEntry(int index) {
		// If there's a parent entry, then return that for zero'th item, else
		// top entry.
		if (mParentEntry != null) {
			return index == 0 ? mParentEntry : mTopEntry;
		} else {
			return mTopEntry;
		}
	}

	/**
	 * Returns the number of entries before the backstack, including the title
	 * of the current fragment and any custom parent title that was set.
	 */
	private int getPreEntryCount() {
		return (mTopEntry != null ? 1 : 0) + (mParentEntry != null ? 1 : 0);
	}

	@Override
	public void onBackStackChanged() {
		updateCrumbs();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// Eventually we should implement our own layout of the views,
		// rather than relying on a linear layout.
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

		// Find rightmost and bottom-most child
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

		// Account for padding too
		maxWidth += getPaddingLeft() + getPaddingRight();
		maxHeight += getPaddingTop() + getPaddingBottom();

		// Check against our minimum height and width
		maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
		maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

		setMeasuredDimension(
				resolveSizeAndState(maxWidth, widthMeasureSpec,
						measuredChildState),
				resolveSizeAndState(maxHeight, heightMeasureSpec,
						measuredChildState << MEASURED_HEIGHT_STATE_SHIFT));
	}

	/**
	 * Attach the bread crumbs to their activity. This must be called once when
	 * creating the bread crumbs.
	 */
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

	/**
	 * The maximum number of breadcrumbs to show. Older fragment headers will be
	 * hidden from view.
	 * 
	 * @param visibleCrumbs
	 *            the number of visible breadcrumbs. This should be greater than
	 *            zero.
	 */
	public void setMaxVisible(int visibleCrumbs) {
		if (visibleCrumbs < 1) {
			throw new IllegalArgumentException(
					"visibleCrumbs must be greater than zero");
		}
		mMaxVisible = visibleCrumbs;
	}

	/**
	 * Sets a listener for clicks on the bread crumbs. This will be called
	 * before the default click action is performed.
	 * 
	 * @param listener
	 *            The new listener to set. Replaces any existing listener.
	 */
	public void setOnBreadCrumbClickListener(OnBreadCrumbClickListener listener) {
		mOnBreadCrumbClickListener = listener;
	}

	/**
	 * Inserts an optional parent entry at the first position in the
	 * breadcrumbs. Selecting this entry will result in a call to the specified
	 * listener's {@link android.view.View.OnClickListener#onClick(View)}
	 * method.
	 * 
	 * @param title
	 *            the title for the parent entry
	 * @param shortTitle
	 *            the short title for the parent entry
	 * @param listener
	 *            the {@link android.view.View.OnClickListener} to be called
	 *            when clicked. A null will result in no action being taken when
	 *            the parent entry is clicked.
	 */
	public void setParentTitle(CharSequence title, CharSequence shortTitle,
			OnClickListener listener) {
		mParentEntry = createBackStackEntry(title, shortTitle);
		mParentClickListener = listener;
		updateCrumbs();
	}

	/**
	 * Set a custom title for the bread crumbs. This will be the first entry
	 * shown at the left, representing the root of the bread crumbs. If the
	 * title is null, it will not be shown.
	 */
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
		// Adjust the visibility and availability of the bread crumbs and
		// divider
		for (int i = 0; i < numViews; i++) {
			final View child = mContainer.getChildAt(i);
			// Disable the last one
			child.findViewById(R.id.title).setEnabled(i < numViews - 1);
			if (mMaxVisible > 0) {
				// Make only the last mMaxVisible crumbs visible
				child.setVisibility(i < numViews - mMaxVisible ? View.GONE
						: View.VISIBLE);
				final View leftIcon = child.findViewById(R.id.left_icon);
				// Remove the divider for all but the last mMaxVisible - 1
				leftIcon.setVisibility(i > numViews - mMaxVisible && i != 0 ? View.VISIBLE
						: View.GONE);
			}
		}
	}
}