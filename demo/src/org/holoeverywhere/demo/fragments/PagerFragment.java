
package org.holoeverywhere.demo.fragments;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.demo.R;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class PagerFragment extends Fragment {
    private final class ListNavigationAdapter extends FragmentPagerAdapter {
        private final List<NavigationItem> list;

        public ListNavigationAdapter() {
            this(new ArrayList<NavigationItem>());
        }

        public ListNavigationAdapter(List<NavigationItem> list) {
            super(getSupportFragmentManager());
            this.list = list;
        }

        public void add(Class<? extends Fragment> clazz, int title) {
            list.add(new NavigationItem(clazz, title));
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            getSupportActivity().getSupportActionBar().setSubtitle(list.get(position).title);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position).getFragment();
        }
    }

    private static final class NavigationItem {
        public final Class<? extends Fragment> clazz;
        private Fragment fragment;
        public final int title;

        public NavigationItem(Class<? extends Fragment> clazz, int title) {
            this.clazz = clazz;
            this.title = title;
        }

        public Fragment getFragment() {
            if (fragment == null) {
                try {
                    fragment = clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return fragment;
        }
    }

    private ListNavigationAdapter adapter;
    private ViewPager pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return pager = new ViewPager(getActivity());
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);

        adapter = new ListNavigationAdapter();
        adapter.add(MainFragment.class, R.string.demo);
        adapter.add(SettingsFragment.class, R.string.settings);
        adapter.add(OtherFragment.class, R.string.other);
        adapter.add(AboutFragment.class, R.string.about);

        pager.setAdapter(adapter);
    }
}
