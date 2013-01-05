
package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class DevelopersFragment extends ListFragment {
    private static final class Developer {
        private final int name, description;

        private final OnClickListener onClickListener;

        public Developer(int name, int description, OnClickListener onClickListener) {
            this.name = name;
            this.description = description;
            this.onClickListener = onClickListener;
        }
    }

    private final class DevelopersAdapter extends ArrayAdapter<Developer> {
        public DevelopersAdapter() {
            super(getSupportActivity(), android.R.id.text1);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.developer);
            }
            TextView name = (TextView) convertView.findViewById(R.id.developerName);
            TextView description = (TextView) convertView.findViewById(R.id.developerDescription);
            Developer developer = getItem(position);
            name.setText(developer.name);
            description.setText(developer.description);
            return convertView;
        }
    }

    private final class EmailListener implements OnClickListener {
        private final String subject, to;

        public EmailListener(String to, String subject) {
            this.to = to;
            this.subject = subject;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {
                    to
            });
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent = Intent.createChooser(intent, getText(R.string.select_email_programm));
            if (intent != null) {
                getActivity().startActivity(intent);
            }
        }
    }

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

    private DevelopersAdapter createDevelopersAdapter() {
        DevelopersAdapter adapter = new DevelopersAdapter();
        prepareAdapter(adapter);
        return adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(createDevelopersAdapter());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Developer developer = (Developer) getListAdapter().getItem(position);
        if (developer.onClickListener != null) {
            developer.onClickListener.onClick(v);
        }
    }

    private void prepareAdapter(DevelopersAdapter adapter) {
        adapter.add(new Developer(R.string.developer_christophe,
                R.string.developer_christophe_description, new UrlListener(
                        "https://plus.google.com/108315424589085456181")));
        adapter.add(new Developer(R.string.developer_sergey,
                R.string.developer_sergey_description, new EmailListener(
                        "prototypegamez@gmail.com", "HoloEverywhere")));
    }
}
