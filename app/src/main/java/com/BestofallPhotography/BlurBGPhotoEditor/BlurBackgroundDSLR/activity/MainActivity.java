package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.net.Uri;

import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;
import java.util.List;

import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.R;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper.mp4u;


public class MainActivity extends Activity implements View.OnClickListener {


    private AdView mAdView;
    InterstitialAd mInterstitialAd;

    public static float screenWidth, screenHeight;
    private Intent intent;
    PermissionManager permission;

    private final int SELECT_PICTURE_BY_GALLERY = 200;

    private LinearLayout shapeBlur;
    private LinearLayout startBlur;
    private LinearLayout myCreation;
    private LinearLayout moreApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        screenWidth = (float) dimension.widthPixels;
        screenHeight = (float) dimension.heightPixels;

        checkPermission();




        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID));

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        if (mp4u.hasInternet(MainActivity.this)) {
            mAdView.setVisibility(View.VISIBLE);
            loadadd();
        } else {
            mAdView.setVisibility(View.GONE);
        }


        startBlur = (LinearLayout) findViewById(R.id.start_blur);
        shapeBlur = (LinearLayout) findViewById(R.id.shape_blur);
        myCreation = (LinearLayout) findViewById(R.id.myCreation);
        moreApps = (LinearLayout) findViewById(R.id.moreApps);
        startBlur.setOnClickListener(this);
        shapeBlur.setOnClickListener(this);
        myCreation.setOnClickListener(this);
        moreApps.setOnClickListener(this);


    }


    private boolean checkStoragePermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkCameraPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    private void checkPermission() {
        permission = new PermissionManager() {

            @Override
            public List<String> setPermission() {
                // If You Don't want to check permission automatically and check your own custom permission
                // Use super.setPermission(); or Don't override this method if not in use
                List<String> customPermission = new ArrayList<>();
                customPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                customPermission.add(Manifest.permission.CAMERA);
                return customPermission;
            }
        };


        //To initiate checking permission
        permission.checkAndRequestPermissions(this);


    }

    private void loadadd() {
        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_google));

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }

        });
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mInterstitialAd != null) {
            mInterstitialAd.setAdListener(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_blur:
                checkPermission();
                Intent intent = new Intent(MainActivity.this, BlurActivity.class);
                startActivity(intent);
                break;
            case R.id.shape_blur:
                checkPermission();
                Intent intent1 = new Intent(MainActivity.this, ShapeBlurActivity.class);
                startActivity(intent1);
                break;
            case R.id.myCreation:

                checkPermission();
                Intent intent3 = new Intent(MainActivity.this, MyCreationActivity.class);
                startActivity(intent3);
                break;
            case R.id.moreApps:
                mp4u.moreApplication(MainActivity.this);
                break;
        }
    }

    public void PPOnClick(View view) {
        Intent moreIntent=new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://1bestofall.com/blurbg-privacy-policy/" ));
        startActivity(moreIntent);
    }
}



