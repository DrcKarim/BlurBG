package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper;

import android.app.Application;
import android.content.Context;



public class AdmobApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }



}
