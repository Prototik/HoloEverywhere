package org.holoeverywhere.issues.i641;

import android.os.Bundle;
import android.view.View;

import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.issues.R;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.ProgressBar;


public class MainActivity extends ListActivity implements View.OnClickListener {
    private View mHeaderView, mFooterView, mRefreshView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setForceHeaderListAdapter(true);
        setListAdapter(new ArrayAdapter<CharSequence>(this, R.layout.simple_list_item_1,
                new CharSequence[]{"Item #1", "Item #2", "Item #3", "Item #4", "Item #5", "Item #6"}));
        getListView().addHeaderView(mHeaderView = makeButton(), null, true);
        getListView().addFooterView(mFooterView = makeButton(), null, true);
    }

    private View makeButton() {
        Button button = new Button(this);
        button.setText("Refresh");
        button.setOnClickListener(this);
        return button;
    }

    @Override
    public void onClick(View v) {
        if (mRefreshView != null) {
            return;
        }
        mRefreshView = new ProgressBar(this, null, R.attr.indeterminateProgressStyle);
        getListView().addFooterView(mRefreshView, null, false);
        getUserHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getListView().removeFooterView(mRefreshView);
                mRefreshView = null;
            }
        }, 5000);
    }
}
