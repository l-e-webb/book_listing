package com.tangledwebgames.booklisting;

import android.text.TextUtils;
import android.util.Log;

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

public class BookQueryUtil {

    static final String LOG_TAG = BookQueryUtil.class.getSimpleName();

    private static final String GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final int MAX_RESULTS = 25;

    private static final int READ_TIMEOUT_TIME = 10000;
    private static final int CONNECT_TIMEOUT_RIME = 15000;

    private static final String BOOK_LIST_KEY = "items";
    private static final String VOLUME_INFO_KEY = "volumeInfo";
    private static final String TITLE_KEY = "title";
    private static final String AUTHORS_KEY = "authors";
    private static final String PUBLISHED_DATE_KEY = "publishedDate";

    static List<Book> getBookData(String searchTerm) {
        String urlStr = GOOGLE_BOOKS_BASE_URL + "?" +
                "q=" + searchTerm + "&" +
                "maxResults=" + MAX_RESULTS;
        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error forming URL, unable to send query.", e);
            return null;
        }

        String jsonResponse;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            return null;
        }

        try {
            return parseJsonResponse(jsonResponse);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON response.", e);
            return null;
        }
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT_TIME);
            connection.setConnectTimeout(CONNECT_TIMEOUT_RIME);
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                jsonResponse = readJsonResponse(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error attempting to make HTTP connections.", e);
        } finally {
            if (connection != null) connection.disconnect();
            if (inputStream != null) inputStream.close();
        }
        return jsonResponse;
    }

    private static String readJsonResponse(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName("UTF-8"))
        );
        String line = reader.readLine();
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }
        return output.toString();
    }

    private static List<Book> parseJsonResponse(String jsonResponse) throws JSONException{
        if (TextUtils.isEmpty(jsonResponse)) {
            Log.e(LOG_TAG, "Query received empty response.");
            return null;
        }

        List<Book> books = new ArrayList<>();

        JSONArray bookJsonArray = new JSONObject(jsonResponse).getJSONArray(BOOK_LIST_KEY);
        for (int i = 0; i < bookJsonArray.length(); i++) {
            JSONObject bookJson = bookJsonArray.getJSONObject(i).getJSONObject(VOLUME_INFO_KEY);
            String title = "";
            String[] authors = new String[0];
            String publishedDate = "";
            if (bookJson.has(TITLE_KEY)) title = bookJson.getString(TITLE_KEY);
            if (bookJson.has(PUBLISHED_DATE_KEY))
                publishedDate = parsePublishDateString(bookJson.getString(PUBLISHED_DATE_KEY));
            if (bookJson.has(AUTHORS_KEY)) {
                JSONArray authorJsonArray = bookJson.getJSONArray(AUTHORS_KEY);
                authors = new String[authorJsonArray.length()];
                for (int j = 0; j < authorJsonArray.length(); j++) {
                    authors[j] = authorJsonArray.getString(j);
                }
            }
            books.add(new Book(title, authors, publishedDate));
        }

        return books;
    }

    private static String parsePublishDateString(String jsonDateString) {
        return jsonDateString;
    }
}
