package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper;

import android.media.ExifInterface;
import android.text.TextUtils;

import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.R;

public class ExifUtils {
    private ExifUtils() {
    }

    public static int getExifRotation(String imgPath) {
        try {
            String rotationAmount = new ExifInterface(imgPath).getAttribute("Orientation");
            if (TextUtils.isEmpty(rotationAmount)) {
                return 0;
            }
            switch (Integer.parseInt(rotationAmount)) {
                case 3:
                    return 180;
                case R.styleable.Toolbar_contentInsetEnd :
                    return 90;
                case R.styleable.Toolbar_contentInsetRight :
                    return 270;
                default:
                    return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }
}
