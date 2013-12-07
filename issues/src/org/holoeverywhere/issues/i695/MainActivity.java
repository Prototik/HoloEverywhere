package org.holoeverywhere.issues.i695;

import android.app.SearchManager;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.issues.R;


public class MainActivity extends Activity {
    private SearchRecentSuggestions mSearchRecentSuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchRecentSuggestions = new SearchRecentSuggestions(this, SuggestionProvider.class.getName(), SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.i695_menu, menu);
        MenuItem item = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchRecentSuggestions.saveRecentQuery(query, null);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return true;
    }
}
