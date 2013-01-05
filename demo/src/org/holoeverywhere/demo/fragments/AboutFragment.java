
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.demo.DemoActivity;
import org.holoeverywhere.demo.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {
    private final class UrlListener implements OnClickListener {
        private final Uri uri;

        public UrlListener(String url) {
            uri = Uri.parse(url);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent = Intent.createChooser(intent, getText(R.string.select_browser));
            if (intent != null) {
                getActivity().startActivity(intent);
            }
        }

    }

    private final OnClickListener developersListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            ((DemoActivity) getSupportActivity()).replaceFragment(
                    Fragment.instantiate(DevelopersFragment.class),
                    "developers");
        }
    };

    private final OnClickListener githubListener = new UrlListener(
            "https://github.com/ChristopheVersieux/HoloEverywhere");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about);
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        view.findViewById(R.id.github).setOnClickListener(githubListener);
        view.findViewById(R.id.developers).setOnClickListener(developersListener);
    }
}
