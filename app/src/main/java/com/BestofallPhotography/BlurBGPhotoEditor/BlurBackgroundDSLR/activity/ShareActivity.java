package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.accessibility.AccessibilityEventCompat;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.ByteArrayOutputStream;

import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.R;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper.mp4u;


public class ShareActivity extends Activity implements View.OnClickListener {
    ImageView imageView, shareImage, exit, home, facebook, whatsapp, twitter, instagram;
    Uri imageUri;
    String image_path;
    private AdView mAdView;
    InterstitialAd mInterstitialAd;
    private Intent share;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        if (mp4u.hasInternet(ShareActivity.this)) {
            mAdView.setVisibility(View.VISIBLE);
            loadadd();
        } else {
            mAdView.setVisibility(View.GONE);
        }


        imageView = (ImageView) findViewById(R.id.imageView);
        home = (ImageView) findViewById(R.id.home);
        shareImage = (ImageView) findViewById(R.id.shareImage);
        //exit = (ImageView) findViewById(R.id.exit);
        facebook = (ImageView) findViewById(R.id.facebook);
        whatsapp = (ImageView) findViewById(R.id.whatsapp);
        twitter = (ImageView) findViewById(R.id.twitter);
        instagram = (ImageView) findViewById(R.id.instagram);
        // exit.setOnClickListener(this);
        shareImage.setOnClickListener(this);
        home.setOnClickListener(this);
        facebook.setOnClickListener(this);
        whatsapp.setOnClickListener(this);
        twitter.setOnClickListener(this);
        instagram.setOnClickListener(this);
        image_path = getIntent().getStringExtra("path");
        bitmap = BitmapFactory.decodeFile(image_path);
        //  imageUri = Uri.parse(image_path);
        //imageUri = Uri.parse(BestofallPhotography.filenew.getAbsolutePath());
        imageUri = Uri.parse(image_path);
        imageView.setImageBitmap(bitmap);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home:
                Intent i = new Intent(ShareActivity.this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.facebook:
                share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                Uri uri = getImageUri(ShareActivity.this, bitmap);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.putExtra("android.intent.extra.TEXT", mp4u.app_name + " Create By : " + mp4u.package_name);
                share.setPackage("com.facebook.katana");//package name of the app
                share.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                try {
                    //startActivity(share);
                    startActivity(Intent.createChooser(share, "Share Image with Facebook"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "Facebook have not been installed.", Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.whatsapp:
                share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                Uri uri1 = getImageUri(ShareActivity.this, bitmap);
                share.putExtra(Intent.EXTRA_STREAM, uri1);
                share.putExtra("android.intent.extra.TEXT", mp4u.app_name + " Create By : " + mp4u.package_name);
                share.setPackage("com.whatsapp");//package name of the app

                try {
                    //startActivity(share);
                    startActivity(Intent.createChooser(share, "Share Image with WhatsApp"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.twitter:
                share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                Uri uri2 = getImageUri(ShareActivity.this, bitmap);
                share.putExtra(Intent.EXTRA_STREAM, uri2);
                share.putExtra("android.intent.extra.TEXT", mp4u.app_name + " Create By : " + mp4u.package_name);
                share.setPackage("com.twitter.android");//package name of the app

                try {
                    // startActivity(share);
                    startActivity(Intent.createChooser(share, "Share Image with Twitter"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "Twitter have not been installed.", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.instagram:
                share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                Uri uri3 = getImageUri(ShareActivity.this, bitmap);
                share.putExtra(Intent.EXTRA_STREAM, uri3);
                share.putExtra("android.intent.extra.TEXT", mp4u.app_name + " Create By : " + mp4u.package_name);
                share.setPackage("com.instagram.android");//package name of the app

                try {
                    //startActivity(share);
                    startActivity(Intent.createChooser(share, "Share Image with Instagram"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "Instagram have not been installed.", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.shareImage:
                try {

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.addFlags(AccessibilityEventCompat.TYPE_GESTURE_DETECTION_END);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra("android.intent.extra.TEXT", mp4u.app_name + " Create By : " + mp4u.package_name);
                    Uri uri4 = getImageUri(ShareActivity.this, bitmap);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri4);
                    startActivity(Intent.createChooser(shareIntent, "Share Image to Other..."));


                } catch (Exception e) {

                }
                break;


        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
        return Uri.parse(MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null));
    }

    private void exitto() {
        try {
            new AlertDialog.Builder(ShareActivity.this)
                    .setTitle("Exit !!!")
                    .setMessage("Do you really want to Exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            finish();
                            Intentto();
                            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                            homeIntent.addCategory(Intent.CATEGORY_HOME);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(homeIntent);

                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        } catch (Exception e) {


        }


    }

    private void Intentto() {
        Intent intent = new Intent(ShareActivity.this, MainActivity.class);
        startActivity(intent);
    }


    public boolean hasInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        exitto();


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
}
/*case R.id.exit:
                exitto();
                break;*/