package com.assignment.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestInterface {

    @POST("oauth2/token")
    @FormUrlEncoded
    Call<JsonObject> getAccessToken(@Header("Authorization") String authorization,
                                    @Field("grant_type") String grantType);

    @GET("1.1/search/tweets.json")
    Call<JsonObject> getSearchHashTags(@Header("Authorization") String authorization,
                                    @Query("q") String query,@Query("geocode") String geocode);
}
