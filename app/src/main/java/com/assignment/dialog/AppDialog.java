package com.assignment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.assignment.R;


public class AppDialog extends Dialog {

    private TextView titleText;
    private TextView infoText;
    private Button buttonPrimary;
    private Button buttonSecondary;

    public AppDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_app_main);
        initView();
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private void initView() {
        titleText       =   (TextView)findViewById(R.id.titleText);
        infoText        =   (TextView)findViewById(R.id.infoText);
        buttonPrimary   =   (Button)findViewById(R.id.buttonPrimary);
        buttonSecondary =   (Button)findViewById(R.id.buttonSecondary);
    }

    public void isCancelable(boolean cancelable) {
        setCancelable(cancelable);
    }

    public void setMessage(String message) {
        infoText.setText(message);
    }

    public void addTitle(String title) {
        if (!title.equals("")) {
            titleText.setVisibility(View.VISIBLE);
            titleText.setText(title);
        }
    }

    public void onPrimaryClick(String buttonText, final OnClickCallback callback) {
        buttonPrimary.setText(buttonText);
        buttonPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.clicked("Cancelled");
            }
        });
    }

    public void onSecondaryClick(String buttonText, final OnClickCallback callback) {

        buttonSecondary.setVisibility(View.VISIBLE);
        buttonSecondary.setText(buttonText);
        buttonSecondary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.clicked("Not Cancelled");
            }
        });
    }

    public void showDialog() {
        this.show();
    }

    public interface OnClickCallback {
        void clicked(Object... data);
    }

}
