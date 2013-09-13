package org.holoeverywhere.issues;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import java.util.List;

public class MainActivity extends ListActivity {
    private IssueAdapter mIssueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("org.holoeverywhere.category.ISSUE_LAUNCHER");
        setListAdapter(mIssueAdapter = new IssueAdapter(this, getPackageManager().queryIntentActivities(intent, 0)));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final ActivityInfo info = mIssueAdapter.getItem(position).activityInfo;
        final Intent intent = new Intent();
        intent.setComponent(new ComponentName(info.packageName, info.name));
        startActivity(intent);

    }

    private static final class IssueAdapter extends ArrayAdapter<ResolveInfo> {
        private final PackageManager mPackageManager;
        private final LayoutInflater mLayoutInflater;

        private IssueAdapter(Context context, List<ResolveInfo> issueLaunchers) {
            super(context, 0, issueLaunchers);
            mPackageManager = context.getPackageManager();
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.simple_list_item_1, parent, false);
            }
            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            textView.setText(getItem(position).activityInfo.loadLabel(mPackageManager));
            return convertView;
        }
    }
}
