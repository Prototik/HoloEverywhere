package org.holoeverywhere.issues.i741;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.addTab(actionBar.newTab()
				.setText("Tab 1")
				.setTabListener(new TabListener<MyFragment>(this, "tab_1", MyFragment.class)));
		actionBar.addTab(actionBar.newTab()
				.setText("Tab 2")
				.setTabListener(new TabListener<MyListFragment>(this, "tab_2", MyListFragment.class)));
	}

    public static final class MyFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            TextView tv = new TextView(inflater.getContext());
            tv.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
            return tv;
        }
    }
	
    public static final class MyListFragment extends ListFragment {
    	@Override
    	public void onViewCreated(View view, Bundle savedInstanceState) {
    		super.onViewCreated(view, savedInstanceState);

			String velues[] = new String[] { "error", "here", "wtf?", "error",
					"here", "wtf?", "error", "here", "wtf?", "error", "here",
					"wtf?", "error", "here", "wtf?" };
    		
    		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, velues));
    	}
    }
    
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
    	private final Activity mActivity;
    	private final String mTag;
    	private final Class<T> mClass;
    	private final Bundle mArgs;
    	private Fragment mFragment;

    	public TabListener(Activity activity, String tag, Class<T> clz) {
    		this(activity, tag, clz, null);
    	}

    	public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
    		mActivity = activity;
    		mTag = tag;
    		mClass = clz;
    		mArgs = args;

    		// Check to see if we already have a fragment for this tab, probably
    		// from a previously saved state.  If so, deactivate it, because our
    		// initial state is that a tab isn't shown.
    		mFragment = (Fragment) mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
    		if (mFragment != null && !mFragment.isDetached()) {
    			FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
    			ft.detach(mFragment);
    			ft.commit();
    		}
    	}

    	public void onTabSelected(Tab tab, FragmentTransaction ft) {
    		if (mFragment == null) {
    			mFragment = Fragment.instantiate(mClass, mArgs);
    			ft.add(android.R.id.content, mFragment, mTag);
    		} else {
    			ft.attach(mFragment);
    		}
    	}

    	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    		if (mFragment != null) {
    			ft.detach(mFragment);
    		}
    	}

    	public void onTabReselected(Tab tab, FragmentTransaction ft) {
    	}
    }
}
