
package org.holoeverywhere.demo.fragments.about;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.content.IntentCompat;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.demo.fragments.OtherFragment;
import org.holoeverywhere.widget.ListView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends OtherFragment {
    private final class UrlListener implements OnOtherItemClickListener {
        private final Uri uri;

        public UrlListener(String url) {
            uri = Uri.parse(url);
        }

        @Override
        public void onClick(OtherItem item) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent = IntentCompat.createChooser(intent, getText(R.string.select_browser));
            if (intent != null) {
                getActivity().startActivity(intent);
            }
        }
    }

    private void addItem(String label, String url) {
        addItem(label, new UrlListener(url));
    }

    @Override
    protected CharSequence getTitle() {
        return "About";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ((ListView) view.findViewById(android.R.id.list)).setForceHeaderListAdapter(true);
        return view;
    }

    @Override
    protected void onHandleData() {
        addItem("GitHub", "https://github.com/Prototik/HoloEverywhere");
        addItem("Play Store", "market://details?id=org.holoeverywhere.demo");
        addItem("Developers", DevelopersFragment.class);
        addItem("Open source licenses", LicensesFragment.class);
    }

    @Override
    protected void onPrepareListView(ListView list) {
        list.addHeaderView(getLayoutInflater().inflate(R.layout.about), null, false);
    }
}
