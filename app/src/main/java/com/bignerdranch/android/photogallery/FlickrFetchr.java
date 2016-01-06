package com.bignerdranch.android.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 12/22/2015.
 * Handles the networking for PhotoGallery
 */
public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";

    private static final String API_KEY = "f55905acfce160a08a05c0d94a1d2961";
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    /** Step 1
     * Gets Url results in form of Bytes
     * @param urlSpec URL
     * @return results from URL in Bytes
     */
    public byte[] getUrlBytes (String urlSpec) throws IOException {
        //1.1 Create URL object then connection-objected pointed to URL
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            //1.2 Setup Input and Output streams
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            //1.2.1 Check connection isn't bad
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }
            //1.3 Read until connection runs out of data
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0)
                out.write(buffer, 0, bytesRead);
            //1.4 Close the Stream then give ByteArrayOutputStream
            out.close();
            return out.toByteArray();

        } finally {
            connection.disconnect();
        }
    }

    /** Step 2
     * Converts result from url passed through the parameter into String
     * @param urlSpec url that is passed to method
     * @return result from the URL
     */
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /** Step 6
     * Fetch and Search for photos by returning list
     * @return list we fetched
     */
    public List<GalleryItem> fetchRecentPhotos() {
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItems(url);
    }
    public List<GalleryItem> searchPhotos(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
    }

    /** Step 3
     *  Build the Url then pass it over to parse the data into JSON
     * @return
     */
    private List<GalleryItem> downloadGalleryItems(String url) {

        List<GalleryItem> items = new ArrayList<>();

        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);

            //3.1 Make JSON Object to a url
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItem(items, jsonBody);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items");
        } catch (JSONException je) {
            Log.e(TAG, "Failed to Parse JSON", je);
        }
        return items;
    }

    /** Step 5
     * Appends necessary parameters
     */
    private String buildUrl(String method, String query) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method", method);

        if (method.equals(SEARCH_METHOD)) {
            uriBuilder.appendQueryParameter("text", query);
        }

        return uriBuilder.build().toString();
    }

    /** Step 4
     * Parse the JSON data
     */
    private void parseItem(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException {

        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        //4.1 Traverse JSON Array extracting needed data
        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            //4.2 Assign data to items
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption((photoJsonObject.getString("title")));
            if (!photoJsonObject.has("url_s"))
                continue;
            item.setUrl(photoJsonObject.getString("url_s"));
            //4.3 Add item to list
            items.add(item);
        }
    }
}





