package com.udacity.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.udacity.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    // Inspired by: https://stackoverflow.com/a/4009133/5999847
    public static boolean IsOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;

        if (cm != null)
            netInfo = cm.getActiveNetworkInfo();

        boolean isOnline = netInfo != null && netInfo.isConnectedOrConnecting();

        if (!isOnline)
            UserInterfaceUtils.ShowToastMessage(context.getString(R.string.no_network_message), context);

        return isOnline;
    }

    public static String GetResponseFromUrl(String requestUrlString, String requesterTag) {
        HttpURLConnection urlConnection = null;

        try {
            Uri builtUri = Uri.parse(requestUrlString);
            URL url = new URL(builtUri.toString());
            String responseBody = null;

            //region Inspired by NetworkUtils from course material.
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext())
                responseBody = scanner.next();
            //endregion

            return responseBody;
        } catch (IOException e) {
            Log.e(requesterTag, "Error", e);
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }
}
