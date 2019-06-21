package com.assignment.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Ajay on 12/8/15.
 */
public class NetworkChecker {
    Context con;
    boolean code = false;

    public NetworkChecker(Context con) {
        this.con = con;
    }

    public boolean nwChecker() {
        ConnectivityManager connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
