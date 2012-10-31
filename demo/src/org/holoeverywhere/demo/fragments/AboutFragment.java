package org.holoeverywhere.demo.fragments;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.demo.R;
import org.holoeverywhere.sherlock.SFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class AboutFragment extends SFragment {
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
			intent.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			intent = Intent.createChooser(intent, "Select a email program");
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
			intent = Intent.createChooser(intent, "Select a browser");
			if (intent != null) {
				getActivity().startActivity(intent);
			}
		}

	}

	private final OnClickListener emailListener = new EmailListener(
			"prototypegamez@gmail.com", "HoloEverywhere");
	private final OnClickListener githubListener = new UrlListener(
			"https://github.com/ChristopheVersieux/HoloEverywhere");
	private final OnClickListener gplusListener = new UrlListener(
			"https://plus.google.com/108315424589085456181");

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.about);
	}

	@Override
	public void onViewCreated(View view) {
		super.onViewCreated(view);
		view.findViewById(R.id.github).setOnClickListener(githubListener);
		view.findViewById(R.id.google_plus).setOnClickListener(gplusListener);
		view.findViewById(R.id.email).setOnClickListener(emailListener);
	}
}
