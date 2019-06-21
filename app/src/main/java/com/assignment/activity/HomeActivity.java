package com.assignment.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;


import com.assignment.R;
import com.assignment.adapter.TwitterImageAdapter;
import com.assignment.constants.Constant;
import com.assignment.dialog.AppDialog;
import com.assignment.interface_class.GrantPermission;
import com.assignment.location.AppLocationProvider;
import com.assignment.network.HttpServerBackend;
import com.assignment.network.RestAdapter;
import com.assignment.network.model.HashTagImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.twitter.sdk.android.core.TwitterCore;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import retrofit2.Call;

public class HomeActivity extends BaseActivity  implements SearchView.OnQueryTextListener, View.OnClickListener {

    private MenuItem mSearchItem;
    private AppDialog appDialog;
    private ImageView mLoadingView;
    private Animation mLoadingAnimation;
    private RecyclerView mHashtagsRecyclerView;
    private Button viewmore;
    private TwitterImageAdapter twitterImageAdapter;
    private ArrayList<HashTagImage> hashTagImageArrayList = new ArrayList<>();

    private String access_token = "";

    private double latitude;
    private double longitude;

    private int distance = 10;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private String search_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
    }

    // Find view
    private void initView() {
        mAuth = FirebaseAuth.getInstance();

        mHashtagsRecyclerView = (RecyclerView) findViewById(R.id.hashtagsRecyclerView);
        setupTweetListView();
        mLoadingView        =   (ImageView)findViewById(R.id.loadingLogo);

        findViewById(R.id.buttonTwitterSignout).setOnClickListener(this);

        viewmore = (Button)findViewById(R.id.viewmore);
        viewmore.setOnClickListener(this);

        // Loading animation
        mLoadingAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        getAccessToken();

        // [START check location permission]
        if (Build.VERSION.SDK_INT >= 23.0) {

            checkLocationPermissions(new GrantPermission() {

                @Override
                public void isPermissionGranted(int permissonCode) {
                    if (permissonCode == Constant.PERMISSION_YES) {
                        getCurrentLocation();
                    } else if (permissonCode == Constant.PERMISSION_NO) {
                        handleForGPSPermission();
                    } else if (permissonCode == Constant.PERMISSION_RATIONALE) {
                        handleForGPSPermission();
                    }
                }

            });
        } else {
            getCurrentLocation();
        }
        // [END check location permission]
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSearchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        if (Build.VERSION.SDK_INT >= 23.0) {
            // [START check location permission]
            checkLocationPermissions(new GrantPermission() {

                @Override
                public void isPermissionGranted(int permissonCode) {
                    search_text = query;
                    if (permissonCode == Constant.PERMISSION_YES) {
                        distance = 10;
                        getCurrentLocation();
                        searchText("Bearer " + access_token, query.toString().replaceAll("#","%23"));
                    } else if (permissonCode == Constant.PERMISSION_NO) {
                        handleForGPSPermission();
                    } else if (permissonCode == Constant.PERMISSION_RATIONALE) {
                        handleForGPSPermission();
                    }
                }

            });
        } else {
            distance = 10;
            getCurrentLocation();
            searchText("Bearer " + access_token, query.toString().replaceAll("#","%23"));
        }
        // [END check location permission]
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    // [START Get twitter Access Token]
    private void getAccessToken() {
        Call<JsonObject> call = getMainApplication().getServerBackend(RestAdapter.AuthType.UNAUTHARIZED).getAccessToken(getAuthorizationHeader(),"client_credentials");
        try {
            new HttpServerBackend(getMainApplication()).getData(call, new HttpServerBackend.ResponseListener() {
                @Override
                public void onReturn(JsonObject data, String code) {
                    if (code.equalsIgnoreCase("200")) {
                        access_token = data.get("access_token").getAsString();
                    }else {
                        appDialog = new AppDialog(HomeActivity.this);
                        appDialog.showDialog();
                        appDialog.addTitle(getString(R.string.error));
                        appDialog.setMessage(getString(R.string.something_wrong));
                        appDialog.setCancelable(false);
                        appDialog.onPrimaryClick("Ok", new AppDialog.OnClickCallback() {
                            @Override
                            public void clicked(Object... data) {
                                appDialog.dismiss();
                            }
                        });
                    }
                }

                @Override
                public void noInternet() {
                    super.noInternet();
                    appDialog = new AppDialog(HomeActivity.this);
                    appDialog.showDialog();
                    appDialog.addTitle(getString(R.string.error));
                    appDialog.setMessage(getString(R.string.check_internet));
                    appDialog.setCancelable(false);
                    appDialog.onPrimaryClick("Ok", new AppDialog.OnClickCallback() {
                        @Override
                        public void clicked(Object... data) {
                            appDialog.dismiss();
                        }
                    });
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    // [End Get twitter Access Token]

    // [START search twitter hashtags #]
    private void searchText(String access_token,String query) {

        showLoadingView(true);

        String geocode = String.valueOf(latitude)+","+String.valueOf(longitude)+","+String.valueOf(distance)+"mi";
        Log.e("geocode","geocode=="+geocode);

        Call<JsonObject> call = getMainApplication().getServerBackend(RestAdapter.AuthType.UNAUTHARIZED).getSearchHashTags(access_token,query,geocode);
        try {
            new HttpServerBackend(getMainApplication()).getData(call, new HttpServerBackend.ResponseListener() {
                @Override
                public void onReturn(JsonObject data, String code) {

                    showLoadingView(false);

                    if (code.equalsIgnoreCase("200")) {
                        Log.e("response","response=="+data);
                        getOnlyImageData(data);

                    }else {
                        appDialog = new AppDialog(HomeActivity.this);
                        appDialog.showDialog();
                        appDialog.addTitle(getString(R.string.error));
                        appDialog.setMessage(getString(R.string.something_wrong));
                        appDialog.setCancelable(false);
                        appDialog.onPrimaryClick("Ok", new AppDialog.OnClickCallback() {
                            @Override
                            public void clicked(Object... data) {
                                appDialog.dismiss();
                            }
                        });
                    }
                }

                @Override
                public void noInternet() {
                    super.noInternet();
                    appDialog = new AppDialog(HomeActivity.this);
                    appDialog.showDialog();
                    appDialog.addTitle(getString(R.string.error));
                    appDialog.setMessage(getString(R.string.check_internet));
                    appDialog.setCancelable(false);
                    appDialog.onPrimaryClick("Ok", new AppDialog.OnClickCallback() {
                        @Override
                        public void clicked(Object... data) {
                            appDialog.dismiss();
                        }
                    });
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    // [End search twitter hashtags #]

    // [Start Authorization get]
    private String getAuthorizationHeader() {
        try {
            String consumerKeyAndSecret = getString(R.string.twitter_consumer_key)+":"+getString(R.string.twitter_consumer_secret);
            byte[] data = consumerKeyAndSecret.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.NO_WRAP);

            return "Basic " + base64;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    // [End Authorization get]

    // [Start Loading View]
    private void showLoadingView(boolean visible) {
        showView(mLoadingView, visible);
        if (visible) {
            mLoadingView.startAnimation(mLoadingAnimation);
        } else {
            mLoadingView.clearAnimation();
        }
    }
    // [End Loading View]

    private void showView(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    // [Start Tweet ListView ]
    private void setupTweetListView() {

        mHashtagsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    // [End Tweet ListView ]

    private void getOnlyImageData(JsonObject data){
        try{
            JsonArray statusesArray = data.get("statuses").getAsJsonArray();

            if (statusesArray.size()==0){
                hashTagImageArrayList.clear();

            }

            for (int index=0;index<statusesArray.size();index++){

                JsonObject dataObject = statusesArray.get(index).getAsJsonObject();

                if (dataObject.has("entities")){

                    JsonObject entitiesObject = dataObject.get("entities").getAsJsonObject();

                    if (entitiesObject.has("media")){

                        JsonArray mediaArray = entitiesObject.get("media").getAsJsonArray();

                        HashTagImage hashTagImage = new HashTagImage();
                        hashTagImage.title  = dataObject.get("text").getAsString();
                        hashTagImage.image  = mediaArray.get(0).getAsJsonObject().get("media_url_https").getAsString();
                        Log.e("asdasdasd","vnbn==="+mediaArray.get(0).getAsJsonObject().get("media_url_https").getAsString());
                        hashTagImageArrayList.add(hashTagImage);

                    }

                }
            }

            if (hashTagImageArrayList.isEmpty()){
                distance = distance+10;
                searchText("Bearer " + access_token, search_text.toString().replaceAll("#","%23"));
            }else{
                viewmore.setVisibility(View.VISIBLE);
                twitterImageAdapter = new TwitterImageAdapter(HomeActivity.this,hashTagImageArrayList);
                mHashtagsRecyclerView.setAdapter(twitterImageAdapter);
            }
            //hashTagImageArrayList.clear();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getCurrentLocation(){
        try {
            AppLocationProvider.requestSingleUpdate(getApplicationContext(),
                    new AppLocationProvider.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(AppLocationProvider.GPSCoordinates location) {
                            latitude = location.latitude;
                            longitude = location.longitude;

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("jxns", "x" + e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonTwitterSignout) {
            signOut();
        }else if (i==R.id.viewmore){
            distance = distance+10;
            searchText("Bearer " + access_token, search_text.toString().replaceAll("#","%23"));
        }
    }

    private void signOut() {
        mAuth.signOut();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        HomeActivity.this.finish();

    }
}
