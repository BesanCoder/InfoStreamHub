package com.Beso.infostreamhub;


import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils extends AppCompatActivity {

    //Tag for the log messages.
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();


    private QueryUtils() {
    }

    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link News}
        List<News> newsItem = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}
        return newsItem;
    }


     // Returns new URL object from the given string URL.

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


     // Make an HTTP request to the given URL and return a String as the response.

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset
                    .forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList where the news will be added.
        List<News> newsItem = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject newsResponse = baseJsonResponse.getJSONObject("response");
            JSONArray newsArray = newsResponse.getJSONArray("results");

            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject currentNewsItem = newsArray.getJSONObject(i);
                String section = currentNewsItem.getString("sectionName");
                String title = currentNewsItem.getString("webTitle");
                String time = currentNewsItem.getString("webPublicationDate");

                // Check if "tags" array is present and not empty
                JSONArray tags = currentNewsItem.optJSONArray("tags");
                String author = "";
                if (tags != null && tags.length() > 0) {
                    // Assuming the first tag contains author information
                    JSONObject tagsDetails = tags.getJSONObject(0);
                    author = tagsDetails.getString("webTitle");
                }

                String url = currentNewsItem.getString("webUrl");

                News news = new News(section, title, time, author, url);
                newsItem.add(news);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        return newsItem;
    }

}
