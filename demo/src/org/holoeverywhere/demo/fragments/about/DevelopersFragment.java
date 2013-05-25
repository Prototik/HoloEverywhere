
package org.holoeverywhere.demo.fragments.about;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.content.IntentCompat;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.AdapterView.OnItemClickListener;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class DevelopersFragment extends ListFragment {
    private static final class Developer {
        private final int description, name;
        private final List<DeveloperLink> links = new ArrayList<DeveloperLink>();

        public Developer(int name, int description) {
            this.name = name;
            this.description = description;
        }

        public Developer link(DeveloperLink link) {
            links.add(link);
            return this;
        }
    }

    private static class DeveloperLink {
        public CharSequence text;

        public void onClick() {

        }
    }

    private final class DeveloperLinksAdapter extends ArrayAdapter<DeveloperLink> implements
            OnItemClickListener {
        private Developer developer;

        public DeveloperLinksAdapter(Developer developer) {
            super(getSupportActivity(), android.R.id.text1);
            this.developer = developer;
            addAll(developer.links);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.simple_list_item_1);
            }
            TextView text = (TextView) convertView.findViewById(android.R.id.text1);
            DeveloperLink link = getItem(position);
            text.setText(link.text);
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.developer_name);
            }
            TextView name = (TextView) convertView.findViewById(android.R.id.text1);
            name.setText(developer.name);
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            getItem(position).onClick();
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
            Spinner name = (Spinner) convertView.findViewById(R.id.developerName);
            TextView description = (TextView) convertView.findViewById(R.id.developerDescription);
            Developer developer = getItem(position);
            DeveloperLinksAdapter linksAdapter = new DeveloperLinksAdapter(developer);
            name.setAdapter(linksAdapter);
            name.internalSetOnItemClickListener(linksAdapter);
            description.setText(developer.description);
            return convertView;
        }
    }

    private class EmailLink extends DeveloperLink {
        private final String subject, to;

        public EmailLink(String to, String subject) {
            this.to = to;
            this.subject = subject;
            text = "Email";
        }

        @Override
        public void onClick() {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {
                    to
            });
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent = IntentCompat.createChooser(intent, getText(R.string.select_email_programm));
            if (intent != null) {
                getActivity().startActivity(intent);
            }
        }
    }

    private class GithubLink extends UrlLink {
        public GithubLink(String username) {
            super("https://github.com/" + username + "/");
            text = "GitHub";
        }
    }

    private final class GPlusLink extends UrlLink {
        public GPlusLink(String userId) {
            super("https://plus.google.com/" + userId + "/posts");
            text = "Google Plus";
        }
    }

    private class HabrahabrLink extends UrlLink {
        public HabrahabrLink(String username) {
            super("http://habrahabr.ru/users/" + username + "/");
            text = "Habrahabr";
        }
    }

    private class UrlLink extends DeveloperLink {
        private final Uri uri;

        public UrlLink(String url) {
            uri = Uri.parse(url);
        }

        @Override
        public void onClick() {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent = IntentCompat.createChooser(intent, getText(R.string.select_browser));
            if (intent != null) {
                getActivity().startActivity(intent);
            }
        }
    }

    private DevelopersAdapter mAdapter;

    private void add(Developer developer) {
        mAdapter.add(developer);
    }

    private DevelopersAdapter createDevelopersAdapter() {
        final DevelopersAdapter adapter = mAdapter = new DevelopersAdapter();
        prepareAdapter(adapter);
        mAdapter = null;
        return adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(createDevelopersAdapter());
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setSubtitle("Developers");
    }

    private void prepareAdapter(DevelopersAdapter adapter) {
        Developer developer;

        developer = new Developer(R.string.developer_sergey, R.string.developer_sergey_description);
        developer.link(new GPlusLink("103272077758668000975"));
        developer.link(new GithubLink("Prototik"));
        developer.link(new EmailLink("prototypegamez@gmail.com", "HoloEverywhere"));
        developer.link(new HabrahabrLink("Prototik"));
        add(developer);

        developer = new Developer(R.string.developer_christophe,
                R.string.developer_christophe_description);
        developer.link(new GPlusLink("108315424589085456181"));
        developer.link(new GithubLink("ChristopheVersieux"));
        add(developer);
    }
}
