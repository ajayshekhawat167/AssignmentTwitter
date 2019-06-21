package com.assignment.network;

import android.util.Log;

import com.assignment.R;
import com.assignment.application.MainApplication;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Ajay on 15/9/16.
 */

public class HttpServerBackend {

    private final MainApplication context;
    String message = "Some error occurred, Try again later";
    Integer errorCode = -10;
    private int totalFileSize;

    public HttpServerBackend(MainApplication context) {
        this.context = context;
    }

    public void getData(final Call<JsonObject> call, final ResponseListener back) {

        if (new NetworkChecker(context).nwChecker()) {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    try {
                        Log.e("Response_", response.body() + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (response.code() == 200) {
                        try {
                            back.onReturn(response.body(),
                                   "200");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        back.onReturn( null, message);
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    back.onReturn( null, message);
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            errorCode = -50;
            back.noInternet();
        }
    }


    public static class ResponseListener {
        public ResponseListener() {
        }

        public void onReturn(JsonObject jsonObject, String code) {
        }

        public void updateProgress(float x) {
        }

        public void noInternet() {
        }
    }
}
