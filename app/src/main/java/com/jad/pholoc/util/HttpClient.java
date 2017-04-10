package com.jad.pholoc.util;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * HTTP Client
 *
 * @author Jorge ALvarado
 */
public class HttpClient {
    /**
     * Open URL
     *
     * @param url           Url
     * @param requestMethod Request Method
     * @return Response
     */
    public static synchronized String openURL(String url, RequestMethod requestMethod) {
        HttpURLConnection urlConnection = null;
        InputStream is = null;
        String response = null;
        try {
            URL u = new URL(url);
            // Configure and open the connection
            urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setRequestMethod(requestMethod.toString());
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            is = urlConnection.getInputStream();

            // If we get a response from the server
            if (is != null) {
                // Get the Response
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line += "\n";
                    sb.append(line);
                }
                response = sb.toString();
            }
        } catch (Exception e) {
            Log.e("HttpClient", e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }
}