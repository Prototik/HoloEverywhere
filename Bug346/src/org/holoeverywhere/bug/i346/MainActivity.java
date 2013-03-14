
package org.holoeverywhere.bug.i346;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.bug.i346.fragment.BugParentFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MainActivity extends Activity {
    private final class MainAdapter extends FragmentPagerAdapter {
        public MainAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putInt("position", position);
            return Fragment.instantiate(BugParentFragment.class, args);
        }
    }

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mViewPager.setAdapter(new MainAdapter());
    }
}
