package com.assignment.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.assignment.R;
import com.assignment.application.MainApplication;
import com.assignment.constants.Constant;
import com.assignment.dialog.AppDialog;
import com.assignment.interface_class.GrantPermission;

public class BaseActivity extends AppCompatActivity {

    public GrantPermission permissionHandler;
    private static final String TAG = "##BaseActivity##";

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public MainApplication getMainApplication() {
        return (MainApplication) getApplicationContext();
    }

    // Check location permissions and perform action accordingly.
    public void checkLocationPermissions(GrantPermission locationHandler) {

        this.permissionHandler = locationHandler;

        try {
            // Google API
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constant.PERMISSION_LOCATION_REQUEST_CODE);
            } else {
                locationHandler.isPermissionGranted(Constant.PERMISSION_YES);
            }

        } catch (Exception e) {
            Log.e(TAG, "InvalidPermission");
            locationHandler.isPermissionGranted(Constant.PERMISSION_NO);
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            //Location permission callback
            case Constant.PERMISSION_LOCATION_REQUEST_CODE: {

                boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);

                if (!showRationale) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionHandler.isPermissionGranted(Constant.PERMISSION_YES);
                    } else {
                        permissionHandler.isPermissionGranted(Constant.PERMISSION_NO);
                    }
                } else {
                    permissionHandler.isPermissionGranted(Constant.PERMISSION_RATIONALE);
                }

                break;
            }
        }

        permissionHandler = null;

    }

    //Alert dialog for gps permission allow
    public void handleForGPSPermission() {

        final AppDialog dialog = new AppDialog(BaseActivity.this);
        dialog.show();
        dialog.addTitle(getResources().getString(R.string.permissionTitle));
        dialog.setMessage(getResources().getString(R.string.gps_permission));
        dialog.isCancelable(false);
        dialog.onPrimaryClick(getResources().getString(R.string.settingButton), new AppDialog.OnClickCallback() {
            @Override
            public void clicked(Object... data) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getMainApplication().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.onSecondaryClick(getResources().getString(R.string.exitButton), new AppDialog.OnClickCallback() {
            @Override
            public void clicked(Object... data) {
                dialog.dismiss();
                finish();
            }
        });
    }

}