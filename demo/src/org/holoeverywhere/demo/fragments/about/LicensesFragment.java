
package org.holoeverywhere.demo.fragments.about;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class LicensesFragment extends ListFragment {
    private static final class License {
        private String mLicenseText;
        private final int mLicenseTextRawId;
        private final String mProject;

        public License(String project, int licenseTextRawId) {
            mProject = project;
            mLicenseTextRawId = licenseTextRawId;
        }
    }

    private final class LicensesAdapter extends ArrayAdapter<License> {
        public LicensesAdapter() {
            super(getSupportActivity(), 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.license, parent, false);
            }
            License license = getItem(position);
            TextView projectName = (TextView) convertView.findViewById(R.id.project);
            projectName.setText(license.mProject);
            TextView licenseText = (TextView) convertView.findViewById(R.id.licenseText);
            if (license.mLicenseText == null) {
                try {
                    InputStream is = getSupportActivity().getResources().openRawResource(
                            license.mLicenseTextRawId);
                    Reader reader = new InputStreamReader(is, "utf-8");
                    StringBuilder builder = new StringBuilder();
                    char[] buffer = new char[1024];
                    int c;
                    while ((c = reader.read(buffer)) > 0) {
                        builder.append(buffer, 0, c);
                    }
                    license.mLicenseText = builder.toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            licenseText.setText(license.mLicenseText);
            return convertView;
        }
    }

    private LicensesAdapter mAdapter;

    private void addLicenses() {
        mAdapter.add(new License("HoloEverywhere", R.raw.license_mit));
        mAdapter.add(new License("ActionBarSherlock", R.raw.license_apache2));
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setSubtitle("Open source licenses");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(mAdapter = new LicensesAdapter());
        addLicenses();
    }
}
