package com.bignerdranch.android.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Daniel on 1/6/2016.
 * Share the query across the entire application
 */
public class QueryPreferences {
    //Key for query preference
    private static final String PREF_SEARCH_QUERY = "searchQuery";

    //returns query value stored in shared preference
    public static String getStroredQuery( Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SEARCH_QUERY, null);
    }

    //writes input query to default preference
    public static void setStoredQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SEARCH_QUERY, query).apply();
    }
}
