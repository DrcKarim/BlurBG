package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.R;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.adapter.CreationAdapter;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper.ExifHelper;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper.mp4u;


public class MyCreationActivity extends Activity {


    private AdView mAdView;
    InterstitialAd mInterstitialAd;

    private ImageView bback;
    private CreationAdapter galleryAdapter;
    private GridView lstList;
    private ImageView noImage;

    ExifHelper exif = new ExifHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_creation);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        if (mp4u.hasInternet(MyCreationActivity.this)) {
            mAdView.setVisibility(View.VISIBLE);
            loadadd();
        } else {
            mAdView.setVisibility(View.GONE);
        }


    }


    protected void onResume() {
        super.onResume();
        bindView();
    }
    private void bindView() {
        noImage = (ImageView) findViewById(R.id.novideoimg);
        lstList = (GridView) findViewById(R.id.lstList);
        getImages();
        if (mp4u.IMAGEALLARY.size() <= 0) {
            this.noImage.setVisibility(View.VISIBLE);
            lstList.setVisibility(View.GONE);
        } else {
            this.noImage.setVisibility(View.GONE);
            lstList.setVisibility(View.VISIBLE);
        }
        bback = (ImageView) findViewById(R.id.back);
        bback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Collections.sort(mp4u.IMAGEALLARY);
        Collections.reverse(mp4u.IMAGEALLARY);
        Log.e("list size", String.valueOf(mp4u.IMAGEALLARY.size()));
        this.galleryAdapter = new CreationAdapter(this, mp4u.IMAGEALLARY);
        lstList.setAdapter(galleryAdapter);
    }
    private void getImages() {
        if (Build.VERSION.SDK_INT < 23) {
            fetchImage();
        } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            fetchImage();
        } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 5);
        }
    }
    private void fetchImage() {
        mp4u.IMAGEALLARY.clear();
        mp4u.listAllImages(new File("/mnt/sdcard/" + mp4u.Edit_Folder_name + "/"));
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 5:
                if (grantResults[0] == 0) {
                    fetchImage();
                    return;
                } else if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 5);
                    return;
                } else {
                    return;
                }
            default:
                return;
        }
    }


  public void saveExif(Uri contentUri) throws IOException {
        String filename = getContentFilename(contentUri);
        if (filename != null) {
            String versionString = null;
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                if (pInfo != null) {
                    versionString = pInfo.versionName;
                }
            } catch (PackageManager.NameNotFoundException e) {
            }
            if (versionString == null) {
                versionString = "0.0";
            }
            this.exif.setAttribute("DateTime", currentTimeAsExif());
            this.exif.setAttribute("Orientation", "0");
            this.exif.setAttribute("Software", getString(R.string.app_name) + versionString + " (Android)");
            this.exif.setAttribute("Description", "Made with MP4u Apps ");
            this.exif.setAttribute("ImageLength", null);
            this.exif.setAttribute("ImageWidth", null);
            this.exif.setAttribute("ImageHeight", null);
            this.exif.writeExif(filename);
        }
    }
    String currentTimeAsExif() {
        return new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
    }

    String getContentFilename(Uri contentUri) throws IOException {
        if (contentUri == null) {
            return null;
        }
        if ("file".equals(contentUri.getScheme())) {
            return contentUri.getPath();
        }
        Cursor cursor = getContentResolver().query(contentUri, new String[]{"_data"}, null, null, null);
        if (cursor == null) {
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow("_data");
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
