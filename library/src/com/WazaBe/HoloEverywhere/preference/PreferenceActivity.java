package com.WazaBe.HoloEverywhere.preference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.ArrayAdapter;
import com.WazaBe.HoloEverywhere.LayoutInflater;
import com.WazaBe.HoloEverywhere.R;
import com.WazaBe.HoloEverywhere.app.FragmentBreadCrumbs;
import com.WazaBe.HoloEverywhere.app.ListActivity;
import com.WazaBe.HoloEverywhere.util.XmlUtils;
import com.WazaBe.HoloEverywhere.widget.ListView;

public abstract class PreferenceActivity extends ListActivity implements
		PreferenceManager.OnPreferenceTreeClickListener,
		PreferenceFragment.OnPreferenceStartFragmentCallback {
	public static final class Header implements Parcelable {
		public static final Creator<Header> CREATOR = new Creator<Header>() {
			@Override
			public Header createFromParcel(Parcel source) {
				return new Header(source);
			}

			@Override
			public Header[] newArray(int size) {
				return new Header[size];
			}
		};
		public CharSequence breadCrumbShortTitle;
		public int breadCrumbShortTitleRes;
		public CharSequence breadCrumbTitle;
		public int breadCrumbTitleRes;
		public Bundle extras;
		public String fragment;
		public Bundle fragmentArguments;
		public int iconRes;
		public long id = HEADER_ID_UNDEFINED;
		public Intent intent;
		public CharSequence summary;
		public int summaryRes;
		public CharSequence title;

		public int titleRes;

		public Header() {
		}

		Header(Parcel in) {
			readFromParcel(in);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		public CharSequence getBreadCrumbShortTitle(Resources res) {
			if (breadCrumbShortTitleRes != 0) {
				return res.getText(breadCrumbShortTitleRes);
			}
			return breadCrumbShortTitle;
		}

		public CharSequence getBreadCrumbTitle(Resources res) {
			if (breadCrumbTitleRes != 0) {
				return res.getText(breadCrumbTitleRes);
			}
			return breadCrumbTitle;
		}

		public CharSequence getSummary(Resources res) {
			if (summaryRes != 0) {
				return res.getText(summaryRes);
			}
			return summary;
		}

		public CharSequence getTitle(Resources res) {
			if (titleRes != 0) {
				return res.getText(titleRes);
			}
			return title;
		}

		public void readFromParcel(Parcel in) {
			id = in.readLong();
			titleRes = in.readInt();
			title = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
			summaryRes = in.readInt();
			summary = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
			breadCrumbTitleRes = in.readInt();
			breadCrumbTitle = TextUtils.CHAR_SEQUENCE_CREATOR
					.createFromParcel(in);
			breadCrumbShortTitleRes = in.readInt();
			breadCrumbShortTitle = TextUtils.CHAR_SEQUENCE_CREATOR
					.createFromParcel(in);
			iconRes = in.readInt();
			fragment = in.readString();
			fragmentArguments = in.readBundle();
			if (in.readInt() != 0) {
				intent = Intent.CREATOR.createFromParcel(in);
			}
			extras = in.readBundle();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeLong(id);
			dest.writeInt(titleRes);
			TextUtils.writeToParcel(title, dest, flags);
			dest.writeInt(summaryRes);
			TextUtils.writeToParcel(summary, dest, flags);
			dest.writeInt(breadCrumbTitleRes);
			TextUtils.writeToParcel(breadCrumbTitle, dest, flags);
			dest.writeInt(breadCrumbShortTitleRes);
			TextUtils.writeToParcel(breadCrumbShortTitle, dest, flags);
			dest.writeInt(iconRes);
			dest.writeString(fragment);
			dest.writeBundle(fragmentArguments);
			if (intent != null) {
				dest.writeInt(1);
				intent.writeToParcel(dest, flags);
			} else {
				dest.writeInt(0);
			}
			dest.writeBundle(extras);
		}
	}

	private static class HeaderAdapter extends ArrayAdapter<Header> {
		private static class HeaderViewHolder {
			ImageView icon;
			TextView summary;
			TextView title;
		}

		private LayoutInflater mInflater;

		public HeaderAdapter(Context context, List<Header> objects) {
			super(context, 0, objects);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HeaderViewHolder holder;
			View view;
			if (convertView == null) {
				view = mInflater.inflate(R.layout.preference_header_item,
						parent, false);
				holder = new HeaderViewHolder();
				holder.icon = (ImageView) view.findViewById(R.id.icon);
				holder.title = (TextView) view.findViewById(R.id.title);
				holder.summary = (TextView) view.findViewById(R.id.summary);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (HeaderViewHolder) view.getTag();
			}
			Header header = getItem(position);
			holder.icon.setImageResource(header.iconRes);
			holder.title.setText(header.getTitle(getContext().getResources()));
			CharSequence summary = header.getSummary(getContext()
					.getResources());
			if (!TextUtils.isEmpty(summary)) {
				holder.summary.setVisibility(View.VISIBLE);
				holder.summary.setText(summary);
			} else {
				holder.summary.setVisibility(View.GONE);
			}

			return view;
		}
	}

	private static final String BACK_STACK_PREFS = ":android:prefs";
	private static final String CUR_HEADER_TAG = ":android:cur_header";
	public static final String EXTRA_NO_HEADERS = ":android:no_headers";
	private static final String EXTRA_PREFS_SET_BACK_TEXT = "extra_prefs_set_back_text";
	private static final String EXTRA_PREFS_SET_NEXT_TEXT = "extra_prefs_set_next_text";
	private static final String EXTRA_PREFS_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";
	private static final String EXTRA_PREFS_SHOW_SKIP = "extra_prefs_show_skip";
	public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";
	public static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":android:show_fragment_args";
	public static final String EXTRA_SHOW_FRAGMENT_SHORT_TITLE = ":android:show_fragment_short_title";
	public static final String EXTRA_SHOW_FRAGMENT_TITLE = ":android:show_fragment_title";
	private static final int FIRST_REQUEST_CODE = 100;
	public static final long HEADER_ID_UNDEFINED = -1;
	private static final String HEADERS_TAG = ":android:headers";
	private static final int MSG_BIND_PREFERENCES = 1;
	private static final int MSG_BUILD_HEADERS = 2;
	private static final String PREFERENCES_TAG = ":android:preferences";
	private Header mCurHeader;
	private FragmentBreadCrumbs mFragmentBreadCrumbs;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_BIND_PREFERENCES: {
				bindPreferences();
			}
				break;
			case MSG_BUILD_HEADERS: {
				ArrayList<Header> oldHeaders = new ArrayList<Header>(mHeaders);
				mHeaders.clear();
				onBuildHeaders(mHeaders);
				if (getListAdapter() instanceof BaseAdapter) {
					((BaseAdapter) getListAdapter()).notifyDataSetChanged();
				}
				Header header = onGetNewHeader();
				if (header != null && header.fragment != null) {
					Header mappedHeader = findBestMatchingHeader(header,
							oldHeaders);
					if (mappedHeader == null || mCurHeader != mappedHeader) {
						switchToHeader(header);
					}
				} else if (mCurHeader != null) {
					Header mappedHeader = findBestMatchingHeader(mCurHeader,
							mHeaders);
					if (mappedHeader != null) {
						setSelectedHeader(mappedHeader);
					}
				}
			}
				break;
			}
		}
	};
	private final ArrayList<Header> mHeaders = new ArrayList<Header>();
	private FrameLayout mListFooter;
	private Button mNextButton;
	private PreferenceManager mPreferenceManager;

	private ViewGroup mPrefsContainer;

	private Bundle mSavedInstanceState;

	private boolean mSinglePane;

	@Deprecated
	public void addPreferencesFromIntent(Intent intent) {
		requirePreferenceManager();

		setPreferenceScreen(mPreferenceManager.inflateFromIntent(intent,
				getPreferenceScreen()));
	}

	@Deprecated
	public void addPreferencesFromResource(int preferencesResId) {
		requirePreferenceManager();

		setPreferenceScreen(mPreferenceManager.inflateFromResource(this,
				preferencesResId, getPreferenceScreen()));
	}

	private void bindPreferences() {
		final PreferenceScreen preferenceScreen = getPreferenceScreen();
		if (preferenceScreen != null) {
			preferenceScreen.bind(getListView());
			if (mSavedInstanceState != null) {
				super.onRestoreInstanceState(mSavedInstanceState);
				mSavedInstanceState = null;
			}
		}
	}

	Header findBestMatchingHeader(Header cur, ArrayList<Header> from) {
		ArrayList<Header> matches = new ArrayList<Header>();
		for (int j = 0; j < from.size(); j++) {
			Header oh = from.get(j);
			if (cur == oh || cur.id != HEADER_ID_UNDEFINED && cur.id == oh.id) {
				matches.clear();
				matches.add(oh);
				break;
			}
			if (cur.fragment != null) {
				if (cur.fragment.equals(oh.fragment)) {
					matches.add(oh);
				}
			} else if (cur.intent != null) {
				if (cur.intent.equals(oh.intent)) {
					matches.add(oh);
				}
			} else if (cur.title != null) {
				if (cur.title.equals(oh.title)) {
					matches.add(oh);
				}
			}
		}
		final int NM = matches.size();
		if (NM == 1) {
			return matches.get(0);
		} else if (NM > 1) {
			for (int j = 0; j < NM; j++) {
				Header oh = matches.get(j);
				if (cur.fragmentArguments != null
						&& cur.fragmentArguments.equals(oh.fragmentArguments)) {
					return oh;
				}
				if (cur.extras != null && cur.extras.equals(oh.extras)) {
					return oh;
				}
				if (cur.title != null && cur.title.equals(oh.title)) {
					return oh;
				}
			}
		}
		return null;
	}

	@Deprecated
	public Preference findPreference(CharSequence key) {
		if (mPreferenceManager == null) {
			return null;
		}
		return mPreferenceManager.findPreference(key);
	}

	public void finishPreferencePanel(Fragment caller, int resultCode,
			Intent resultData) {
		if (mSinglePane) {
			setResult(resultCode, resultData);
			finish();
		} else {
			onSupportBackPressed();
			if (caller != null) {
				if (caller.getTargetFragment() != null) {
					caller.getTargetFragment().onActivityResult(
							caller.getTargetRequestCode(), resultCode,
							resultData);
				}
			}
		}
	}

	public List<Header> getHeaders() {
		return mHeaders;
	}

	@Override
	public LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(this);
	}

	protected Button getNextButton() {
		return mNextButton;
	}

	@Deprecated
	public PreferenceManager getPreferenceManager() {
		return mPreferenceManager;
	}

	@Deprecated
	public PreferenceScreen getPreferenceScreen() {
		if (mPreferenceManager != null) {
			return mPreferenceManager.getPreferenceScreen();
		}
		return null;
	}

	@Override
	public Object getSystemService(String name) {
		return LayoutInflater.getSystemService(super.getSystemService(name));
	}

	public boolean hasHeaders() {
		return getListView().getVisibility() == View.VISIBLE
				&& mPreferenceManager == null;
	}

	protected boolean hasNextButton() {
		return mNextButton != null;
	}

	public void invalidateHeaders() {
		if (!mHandler.hasMessages(MSG_BUILD_HEADERS)) {
			mHandler.sendEmptyMessage(MSG_BUILD_HEADERS);
		}
	}

	public boolean isMultiPane() {
		return hasHeaders() && mPrefsContainer.getVisibility() == View.VISIBLE;
	}

	public void loadHeadersFromResource(int resid, List<Header> target) {
		XmlResourceParser parser = null;
		try {
			parser = getResources().getXml(resid);
			AttributeSet attrs = Xml.asAttributeSet(parser);

			int type;
			while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
					&& type != XmlPullParser.START_TAG) {
				// Parse next until start tag is found
			}

			String nodeName = parser.getName();
			if (!"preference-headers".equals(nodeName)) {
				throw new RuntimeException(
						"XML document must start with <preference-headers> tag; found"
								+ nodeName + " at "
								+ parser.getPositionDescription());
			}

			Bundle curBundle = null;

			final int outerDepth = parser.getDepth();
			while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
					&& (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
				if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
					continue;
				}

				nodeName = parser.getName();
				if ("header".equals(nodeName)) {
					Header header = new Header();

					TypedArray sa = getResources().obtainAttributes(attrs,
							R.styleable.PreferenceHeader);
					header.id = sa.getResourceId(
							R.styleable.PreferenceHeader_id,
							(int) HEADER_ID_UNDEFINED);
					TypedValue tv = sa
							.peekValue(R.styleable.PreferenceHeader_title);
					if (tv != null && tv.type == TypedValue.TYPE_STRING) {
						if (tv.resourceId != 0) {
							header.titleRes = tv.resourceId;
						} else {
							header.title = tv.string;
						}
					}
					tv = sa.peekValue(R.styleable.PreferenceHeader_summary);
					if (tv != null && tv.type == TypedValue.TYPE_STRING) {
						if (tv.resourceId != 0) {
							header.summaryRes = tv.resourceId;
						} else {
							header.summary = tv.string;
						}
					}
					tv = sa.peekValue(R.styleable.PreferenceHeader_breadCrumbTitle);
					if (tv != null && tv.type == TypedValue.TYPE_STRING) {
						if (tv.resourceId != 0) {
							header.breadCrumbTitleRes = tv.resourceId;
						} else {
							header.breadCrumbTitle = tv.string;
						}
					}
					tv = sa.peekValue(R.styleable.PreferenceHeader_breadCrumbShortTitle);
					if (tv != null && tv.type == TypedValue.TYPE_STRING) {
						if (tv.resourceId != 0) {
							header.breadCrumbShortTitleRes = tv.resourceId;
						} else {
							header.breadCrumbShortTitle = tv.string;
						}
					}
					header.iconRes = sa.getResourceId(
							R.styleable.PreferenceHeader_icon, 0);
					header.fragment = sa
							.getString(R.styleable.PreferenceHeader_fragment);
					sa.recycle();

					if (curBundle == null) {
						curBundle = new Bundle();
					}

					final int innerDepth = parser.getDepth();
					while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
							&& (type != XmlPullParser.END_TAG || parser
									.getDepth() > innerDepth)) {
						if (type == XmlPullParser.END_TAG
								|| type == XmlPullParser.TEXT) {
							continue;
						}

						String innerNodeName = parser.getName();
						if (innerNodeName.equals("extra")) {
							getResources().parseBundleExtra("extra", attrs,
									curBundle);
							XmlUtils.skipCurrentTag(parser);

						} else if (innerNodeName.equals("intent")) {
							header.intent = Intent.parseIntent(getResources(),
									parser, attrs);

						} else {
							XmlUtils.skipCurrentTag(parser);
						}
					}

					if (curBundle.size() > 0) {
						header.fragmentArguments = curBundle;
						curBundle = null;
					}

					target.add(header);
				} else {
					XmlUtils.skipCurrentTag(parser);
				}
			}

		} catch (XmlPullParserException e) {
			throw new RuntimeException("Error parsing headers", e);
		} catch (IOException e) {
			throw new RuntimeException("Error parsing headers", e);
		} finally {
			if (parser != null) {
				parser.close();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (mPreferenceManager != null) {
			mPreferenceManager.dispatchActivityResult(requestCode, resultCode,
					data);
		}
	}

	public void onBuildHeaders(List<Header> target) {
	}

	public Intent onBuildStartFragmentIntent(String fragmentName, Bundle args,
			int titleRes, int shortTitleRes) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClass(this, getClass());
		intent.putExtra(EXTRA_SHOW_FRAGMENT, fragmentName);
		intent.putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, args);
		intent.putExtra(EXTRA_SHOW_FRAGMENT_TITLE, titleRes);
		intent.putExtra(EXTRA_SHOW_FRAGMENT_SHORT_TITLE, shortTitleRes);
		intent.putExtra(EXTRA_NO_HEADERS, true);
		return intent;
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();

		if (mPreferenceManager != null) {
			postBindPreferences();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preference_list_content);
		mListFooter = (FrameLayout) findViewById(R.id.list_footer);
		mPrefsContainer = (ViewGroup) findViewById(R.id.prefs_frame);
		boolean hidingHeaders = onIsHidingHeaders();
		mSinglePane = hidingHeaders || !onIsMultiPane();
		String initialFragment = getIntent()
				.getStringExtra(EXTRA_SHOW_FRAGMENT);
		Bundle initialArguments = getIntent().getBundleExtra(
				EXTRA_SHOW_FRAGMENT_ARGUMENTS);
		int initialTitle = getIntent()
				.getIntExtra(EXTRA_SHOW_FRAGMENT_TITLE, 0);
		int initialShortTitle = getIntent().getIntExtra(
				EXTRA_SHOW_FRAGMENT_SHORT_TITLE, 0);
		if (savedInstanceState != null) {
			ArrayList<Header> headers = savedInstanceState
					.getParcelableArrayList(HEADERS_TAG);
			if (headers != null) {
				mHeaders.addAll(headers);
				int curHeader = savedInstanceState.getInt(CUR_HEADER_TAG,
						(int) HEADER_ID_UNDEFINED);
				if (curHeader >= 0 && curHeader < mHeaders.size()) {
					setSelectedHeader(mHeaders.get(curHeader));
				}
			}
		} else {
			if (initialFragment != null && mSinglePane) {
				switchToHeader(initialFragment, initialArguments);
				if (initialTitle != 0) {
					CharSequence initialTitleStr = getText(initialTitle);
					CharSequence initialShortTitleStr = initialShortTitle != 0 ? getText(initialShortTitle)
							: null;
					showBreadCrumbs(initialTitleStr, initialShortTitleStr);
				}
			} else {
				onBuildHeaders(mHeaders);
				if (mHeaders.size() > 0) {
					if (!mSinglePane) {
						if (initialFragment == null) {
							Header h = onGetInitialHeader();
							switchToHeader(h);
						} else {
							switchToHeader(initialFragment, initialArguments);
						}
					}
				}
			}
		}
		if (initialFragment != null && mSinglePane) {
			findViewById(R.id.headers).setVisibility(View.GONE);
			mPrefsContainer.setVisibility(View.VISIBLE);
			if (initialTitle != 0) {
				CharSequence initialTitleStr = getText(initialTitle);
				CharSequence initialShortTitleStr = initialShortTitle != 0 ? getText(initialShortTitle)
						: null;
				showBreadCrumbs(initialTitleStr, initialShortTitleStr);
			}
		} else if (mHeaders.size() > 0) {
			setListAdapter(new HeaderAdapter(this, mHeaders));
			if (!mSinglePane) {
				getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
				if (mCurHeader != null) {
					setSelectedHeader(mCurHeader);
				}
				mPrefsContainer.setVisibility(View.VISIBLE);
			}
		} else {
			setContentView(R.layout.preference_list_content_single);
			mListFooter = (FrameLayout) findViewById(R.id.list_footer);
			mPrefsContainer = (ViewGroup) findViewById(R.id.prefs);
			mPreferenceManager = new PreferenceManager(this, FIRST_REQUEST_CODE);
			mPreferenceManager.setOnPreferenceTreeClickListener(this);
		}

		Intent intent = getIntent();
		if (intent.getBooleanExtra(EXTRA_PREFS_SHOW_BUTTON_BAR, false)) {

			findViewById(R.id.button_bar).setVisibility(View.VISIBLE);

			Button backButton = (Button) findViewById(R.id.back_button);
			backButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});
			Button skipButton = (Button) findViewById(R.id.skip_button);
			skipButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(RESULT_OK);
					finish();
				}
			});
			mNextButton = (Button) findViewById(R.id.next_button);
			mNextButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(RESULT_OK);
					finish();
				}
			});

			if (intent.hasExtra(EXTRA_PREFS_SET_NEXT_TEXT)) {
				String buttonText = intent
						.getStringExtra(EXTRA_PREFS_SET_NEXT_TEXT);
				if (TextUtils.isEmpty(buttonText)) {
					mNextButton.setVisibility(View.GONE);
				} else {
					mNextButton.setText(buttonText);
				}
			}
			if (intent.hasExtra(EXTRA_PREFS_SET_BACK_TEXT)) {
				String buttonText = intent
						.getStringExtra(EXTRA_PREFS_SET_BACK_TEXT);
				if (TextUtils.isEmpty(buttonText)) {
					backButton.setVisibility(View.GONE);
				} else {
					backButton.setText(buttonText);
				}
			}
			if (intent.getBooleanExtra(EXTRA_PREFS_SHOW_SKIP, false)) {
				skipButton.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mPreferenceManager != null) {
			mPreferenceManager.dispatchActivityDestroy();
		}
	}

	public Header onGetInitialHeader() {
		return mHeaders.get(0);
	}

	public Header onGetNewHeader() {
		return null;
	}

	public void onHeaderClick(Header header, int position) {
		if (header.fragment != null) {
			if (mSinglePane) {
				int titleRes = header.breadCrumbTitleRes;
				int shortTitleRes = header.breadCrumbShortTitleRes;
				if (titleRes == 0) {
					titleRes = header.titleRes;
					shortTitleRes = 0;
				}
				startWithFragment(header.fragment, header.fragmentArguments,
						null, 0, titleRes, shortTitleRes);
			} else {
				switchToHeader(header);
			}
		} else if (header.intent != null) {
			startActivity(header.intent);
		}
	}

	public boolean onIsHidingHeaders() {
		return getIntent().getBooleanExtra(EXTRA_NO_HEADERS, false);
	}

	public boolean onIsMultiPane() {
		boolean preferMultiPane = getResources().getBoolean(
				R.bool.preferences_prefer_dual_pane);
		return preferMultiPane;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (getListAdapter() != null) {
			Object item = getListAdapter().getItem(position);
			if (item instanceof Header) {
				onHeaderClick((Header) item, position);
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (mPreferenceManager != null) {
			mPreferenceManager.dispatchNewIntent(intent);
		}
	}

	@Override
	public boolean onPreferenceStartFragment(PreferenceFragment caller,
			Preference pref) {
		startPreferencePanel(pref.getFragment(), pref.getExtras(),
				pref.getTitleRes(), pref.getTitle(), null, 0);
		return true;
	}

	@Override
	@Deprecated
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		return false;
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		if (mPreferenceManager != null) {
			Bundle container = state.getBundle(PREFERENCES_TAG);
			if (container != null) {
				final PreferenceScreen preferenceScreen = getPreferenceScreen();
				if (preferenceScreen != null) {
					preferenceScreen.restoreHierarchyState(container);
					mSavedInstanceState = state;
					return;
				}
			}
		}
		super.onRestoreInstanceState(state);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mHeaders.size() > 0) {
			outState.putParcelableArrayList(HEADERS_TAG, mHeaders);
			if (mCurHeader != null) {
				int index = mHeaders.indexOf(mCurHeader);
				if (index >= 0) {
					outState.putInt(CUR_HEADER_TAG, index);
				}
			}
		}

		if (mPreferenceManager != null) {
			final PreferenceScreen preferenceScreen = getPreferenceScreen();
			if (preferenceScreen != null) {
				Bundle container = new Bundle();
				preferenceScreen.saveHierarchyState(container);
				outState.putBundle(PREFERENCES_TAG, container);
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (mPreferenceManager != null) {
			mPreferenceManager.dispatchActivityStop();
		}
	}

	private void postBindPreferences() {
		if (mHandler.hasMessages(MSG_BIND_PREFERENCES)) {
			return;
		}
		mHandler.obtainMessage(MSG_BIND_PREFERENCES).sendToTarget();
	}

	private void requirePreferenceManager() {
		if (mPreferenceManager == null) {
			if (mAdapter == null) {
				throw new RuntimeException(
						"This should be called after super.onCreate.");
			}
			throw new RuntimeException(
					"Modern two-pane PreferenceActivity requires use of a PreferenceFragment");
		}
	}

	/**
	 * Set a footer that should be shown at the bottom of the header list.
	 */
	public void setListFooter(View view) {
		mListFooter.removeAllViews();
		mListFooter.addView(view, new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	public void setParentTitle(CharSequence title, CharSequence shortTitle,
			OnClickListener listener) {
		if (mFragmentBreadCrumbs != null) {
			mFragmentBreadCrumbs.setParentTitle(title, shortTitle, listener);
		}
	}

	@Deprecated
	public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
		requirePreferenceManager();

		if (mPreferenceManager.setPreferences(preferenceScreen)
				&& preferenceScreen != null) {
			postBindPreferences();
			CharSequence title = getPreferenceScreen().getTitle();
			if (title != null) {
				setTitle(title);
			}
		}
	}

	void setSelectedHeader(Header header) {
		mCurHeader = header;
		int index = mHeaders.indexOf(header);
		if (index >= 0) {
			getListView().setItemChecked(index, true);
		} else {
			getListView().clearChoices();
		}
		showBreadCrumbs(header);
	}

	public void showBreadCrumbs(CharSequence title, CharSequence shortTitle) {
		if (mFragmentBreadCrumbs == null) {
			View crumbs = findViewById(R.id.title);
			try {
				mFragmentBreadCrumbs = (FragmentBreadCrumbs) crumbs;
			} catch (ClassCastException e) {
				return;
			}
			if (mFragmentBreadCrumbs == null) {
				if (title != null) {
					setTitle(title);
				}
				return;
			}
			mFragmentBreadCrumbs.setMaxVisible(2);
			mFragmentBreadCrumbs.setActivity(this);
		}
		mFragmentBreadCrumbs.setTitle(title, shortTitle);
		mFragmentBreadCrumbs.setParentTitle(null, null, null);
	}

	void showBreadCrumbs(Header header) {
		if (header != null) {
			CharSequence title = header.getBreadCrumbTitle(getResources());
			if (title == null) {
				title = header.getTitle(getResources());
			}
			if (title == null) {
				title = getTitle();
			}
			showBreadCrumbs(title,
					header.getBreadCrumbShortTitle(getResources()));
		} else {
			showBreadCrumbs(getTitle(), null);
		}
	}

	public void startPreferenceFragment(Fragment fragment, boolean push) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.prefs, fragment);
		if (push) {
			transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.addToBackStack(BACK_STACK_PREFS);
		} else {
			transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		}
		transaction.commitAllowingStateLoss();
	}

	public void startPreferencePanel(String fragmentClass, Bundle args,
			int titleRes, CharSequence titleText, Fragment resultTo,
			int resultRequestCode) {
		if (mSinglePane) {
			startWithFragment(fragmentClass, args, resultTo, resultRequestCode,
					titleRes, 0);
		} else {
			Fragment f = Fragment.instantiate(this, fragmentClass, args);
			if (resultTo != null) {
				f.setTargetFragment(resultTo, resultRequestCode);
			}
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.prefs, f);
			if (titleRes != 0) {
				transaction.setBreadCrumbTitle(titleRes);
			} else if (titleText != null) {
				transaction.setBreadCrumbTitle(titleText);
			}
			transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.addToBackStack(BACK_STACK_PREFS);
			transaction.commitAllowingStateLoss();
		}
	}

	public void startWithFragment(String fragmentName, Bundle args,
			Fragment resultTo, int resultRequestCode) {
		startWithFragment(fragmentName, args, resultTo, resultRequestCode, 0, 0);
	}

	public void startWithFragment(String fragmentName, Bundle args,
			Fragment resultTo, int resultRequestCode, int titleRes,
			int shortTitleRes) {
		Intent intent = onBuildStartFragmentIntent(fragmentName, args,
				titleRes, shortTitleRes);
		if (resultTo == null) {
			startActivity(intent);
		} else {
			resultTo.startActivityForResult(intent, resultRequestCode);
		}
	}

	public void switchToHeader(Header header) {
		if (mCurHeader == header) {
			getSupportFragmentManager().popBackStack(BACK_STACK_PREFS,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		} else {
			int direction = mHeaders.indexOf(header)
					- mHeaders.indexOf(mCurHeader);
			switchToHeaderInner(header.fragment, header.fragmentArguments,
					direction);
			setSelectedHeader(header);
		}
	}

	public void switchToHeader(String fragmentName, Bundle args) {
		setSelectedHeader(null);
		switchToHeaderInner(fragmentName, args, 0);
	}

	private void switchToHeaderInner(String fragmentName, Bundle args,
			int direction) {
		getSupportFragmentManager().popBackStack(BACK_STACK_PREFS,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		Fragment f = Fragment.instantiate(this, fragmentName, args);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.prefs, f);
		transaction.commitAllowingStateLoss();
	}
}