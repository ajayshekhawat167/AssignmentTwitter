package com.assignment.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestAdapter {


    String API_BASE_URL = "https://api.twitter.com/";

    private Retrofit restAdapter;
    private RestInterface restInterface;

    public RestAdapter() {
    }

    public RestInterface getRestInterface() {

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(40, TimeUnit.SECONDS)
                .connectTimeout(40, TimeUnit.SECONDS)
                .build();

        restAdapter = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        restInterface = restAdapter.create(RestInterface.class);
        return restInterface;
    }

    //Have to decide about the sessionId
    public RestInterface getAuthRestInterface() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
        Gson gson = new GsonBuilder().setLenient().create();
       // client.interceptors().add(new Htt)
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson)).build();
        restInterface = retrofit.create(RestInterface.class);

        return restInterface;
    }


    public enum AuthType {
        AUTHARIZED, UNAUTHARIZED
    }


}