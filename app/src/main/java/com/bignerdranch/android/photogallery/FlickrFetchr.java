package com.bignerdranch.android.photogallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Daniel on 12/22/2015.
 * Handles the networking for PhotoGallery
 */
public class FlickrFetchr {

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

}
