
package org.holoeverywhere.bug.i391;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {
    private final class MyAdapter extends FragmentStatePagerAdapter {
        MyAdapter() {
            super(getChildFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            String title = null;
            switch (position) {
                case 0:
                    title = "Hell, there are no rules here - we're trying to accomplish something.";
                    break;
                case 1:
                    title = "I find that the harder I work, the more luck I seem to have.";
                    break;
                case 2:
                    title = "Women make love for love, men make love for lust.";
                    break;
                case 3:
                    title = "Everybody wants to save the earth; nobody wants to help Mom do the dishes.";
                    break;
                case 4:
                    title = "Always forgive your enemies, but never forget their names.";
                    break;
            }
            Bundle args = new Bundle();
            args.putString(ChildFragment.KEY_TITLE, title);
            return Fragment.instantiate(ChildFragment.class, args);
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    private ViewPager mPager;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(new MyAdapter());
    }
}
