package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build.VERSION;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class UserPermission {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @TargetApi(16)
    public static boolean checkPermission(final Context context) {
        if (VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        Activity activity = (Activity) context;
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, "android.permission.READ_EXTERNAL_STORAGE")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            builder.setTitle((CharSequence) "Permission necessary");
            builder.setMessage((CharSequence) "External storage permission is necessary");
            builder.setPositiveButton((CharSequence) "Permission", (OnClickListener) new OnClickListener() {
                @TargetApi(16)
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, UserPermission.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
            });
            builder.create().show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        return false;
    }
}
