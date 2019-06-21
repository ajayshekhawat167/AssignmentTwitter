package com.assignment.application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;


import com.assignment.network.RestAdapter;
import com.assignment.network.RestInterface;

import java.security.MessageDigest;


/**
 * Created by Ajay on 10/26/2014.
 */
public class MainApplication extends Application {

    private Activity mCurrentActivity = null;
    private static MainApplication mInstance;

    private RestAdapter apiHelper;


    public void onCreate() {
        super.onCreate();

        if (mInstance == null){
            mInstance = this;
        }

    }

    /**
     * This method is used to get instance of Application
     * @return
     */
    public static synchronized MainApplication getInstance() {
        return mInstance;
    }


    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    private synchronized RestAdapter getServerBackend() {
        if (apiHelper != null)
            return apiHelper;
        try {
            return (apiHelper = new RestAdapter());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Invalid Api Definitions","Definitions");
            throw new RuntimeException("Invalid Api Definitions");
        }
    }

    public RestInterface getServerBackend(RestAdapter.AuthType type) {
        if (type.equals(RestAdapter.AuthType.AUTHARIZED))
            return getServerBackend().getAuthRestInterface();
        else
            return getServerBackend().getRestInterface();

    }

}
