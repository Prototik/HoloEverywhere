package org.holoeverywhere.issues.i695;

import android.content.SearchRecentSuggestionsProvider;

public class SuggestionProvider extends SearchRecentSuggestionsProvider {
    public SuggestionProvider() {
        super();
        setupSuggestions(SuggestionProvider.class.getName(), DATABASE_MODE_QUERIES);
    }
}
