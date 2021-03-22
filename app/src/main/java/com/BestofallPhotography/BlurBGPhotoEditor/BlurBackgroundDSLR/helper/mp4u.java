package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.R;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper.ExifUtils.getExifRotation;


public class mp4u  {
    public static float screenWidth, screenHeight;
    public static Bitmap backgroundBitmap = null;
    public static File newDir = new File(Environment.getExternalStorageDirectory() + "/"+AdmobApplication.getContext().getString(R.string.app_name));
    public static File filenew;
    public static Bitmap photoBitmap=null;
    public static ArrayList<String> IMAGEALLARY = new ArrayList();
    public static String app_name = newDir.getName();
    public static String Edit_Folder_name = AdmobApplication.getContext().getString(R.string.app_name);
    public static String package_name = "https://play.google.com/store/apps/details?id="+AdmobApplication.getContext().getPackageName();

    public static boolean hasInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

    public static int dpToPx(Context c, int dp) {
        float f = (float) dp;
        c.getResources();
        return (int) (f * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float pxToDp(Context context, float px) {
        return px / (((float) context.getResources().getDisplayMetrics().densityDpi) / 160.0f);
    }

    public static Bitmap resizeBitmap1(Bitmap bit, int width, int height) {
        if (bit == null) {
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        float wr = (float) width;
        float hr = (float) height;
        try {
            float wd = (float) bit.getWidth();
            float he = (float) bit.getHeight();

            float rat1 = wd / he;
            float rat2 = he / wd;
            if (wd > wr) {
                wd = wr;
                he = wd * rat2;

                if (he > hr) {
                    he = hr;
                    wd = he * rat1;

                } else {
                    wd = wr;
                    he = wd * rat2;

                }
            } else if (he > hr) {
                he = hr;
                wd = he * rat1;

                if (wd > wr) {
                    wd = wr;
                    he = wd * rat2;
                } else {

                }
            } else if (rat1 > 0.75f) {
                wd = wr;
                he = wd * rat2;

            } else if (rat2 > 1.5f) {
                he = hr;
                wd = he * rat1;

            } else {
                he = wr * rat2;

                if (he > hr) {
                    he = hr;
                    wd = he * rat1;

                } else {
                    wd = wr;
                    he = wd * rat2;

                }
            }
            return Bitmap.createScaledBitmap(bit, (int) wd, (int) he, false);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bit;
        } catch (Exception e2) {
            e2.printStackTrace();
            return bit;
        }
    }

    public static Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }




    public static void shareApplication(Context context) {
        //share a url
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_TEXT, "  Download this app on PlayStore :- https://play.google.com/store/apps/details?id="+context.getPackageName());
        context.startActivity(Intent.createChooser(share, "Share With Your friends!"));

    }

    public static void rateApplication(Context context)
    {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK|
                Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }


    //.....................More app play store code........
    public static void moreApplication(Context context)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://search?q=pub:"+"\""+context.getString(R.string.more_link)+"\""));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void listAllImages(File filepath) {
        File[] files = filepath.listFiles();
        if (files != null) {
            for (int j = files.length - 1; j >= 0; j--) {
                String ss = files[j].toString();
                File check = new File(ss);
                Log.d("" + check.length(), "" + check.length());
                if (check.length() <= PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
                    Log.i("Invalid Image", "Delete Image");
                } else if (check.toString().contains(".jpg") || check.toString().contains(".png") || check.toString().contains(".jpeg")) {
                    IMAGEALLARY.add(ss);
                }
                System.out.println(ss);
            }
            return;
        }
        System.out.println("Empty Folder");
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri, float screenWidth, float screenHeight) throws IOException {
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            BitmapFactory.Options bfo = new BitmapFactory.Options();
            bfo.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, bfo);
            BitmapFactory.Options optsDownSample = new BitmapFactory.Options();
            if (screenWidth <= screenHeight) {
                screenWidth = screenHeight;
            }
            int maxDim = (int) screenWidth;
            optsDownSample.inSampleSize = getClosestResampleSize(bfo.outWidth, bfo.outHeight, maxDim);
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, optsDownSample);
            Matrix m = new Matrix();
            if (image.getWidth() > maxDim || image.getHeight() > maxDim) {
                BitmapFactory.Options optsScale = getResampling(image.getWidth(), image.getHeight(), maxDim);
                m.postScale(((float) optsScale.outWidth) / ((float) image.getWidth()), ((float) optsScale.outHeight) / ((float) image.getHeight()));
            }
            String pathInput = getRealPathFromURI(uri, context);
            if (new Integer(Build.VERSION.SDK).intValue() > 4) {
                int rotation = getExifRotation(pathInput);
                if (rotation != 0) {
                    m.postRotate((float) rotation);
                }
            }
            image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), m, true);
            parcelFileDescriptor.close();
            return image;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getClosestResampleSize(int cx, int cy, int maxDim) {
        int max = Math.max(cx, cy);
        int resample = 1;
        while (resample < Integer.MAX_VALUE) {
            if (resample * maxDim > max) {
                resample--;
                break;
            }
            resample++;
        }
        return resample > 0 ? resample : 1;
    }


    public static BitmapFactory.Options getResampling(int cx, int cy, int max) {
        float scaleVal;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        if (cx > cy) {
            scaleVal = ((float) max) / ((float) cx);
        } else if (cy > cx) {
            scaleVal = ((float) max) / ((float) cy);
        } else {
            scaleVal = ((float) max) / ((float) cx);
        }
        bfo.outWidth = (int) ((((float) cx) * scaleVal) + 0.5f);
        bfo.outHeight = (int) ((((float) cy) * scaleVal) + 0.5f);
        return bfo;
    }

    public static String getRealPathFromURI(Uri contentURI, Context context) {
        try {
            Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) {
                return contentURI.getPath();
            }
            cursor.moveToFirst();
            String result = cursor.getString(cursor.getColumnIndex("_data"));
            cursor.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return contentURI.toString();
        }
    }

    public static Bitmap resizeBitmap(Bitmap bit, int width, int height) {
        if (bit == null) {
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        float wr = (float) width;
        float hr = (float) height;
        try {
            float wd = (float) bit.getWidth();
            float he = (float) bit.getHeight();
            Log.i("testings", wr + "  " + hr + "  and  " + wd + "  " + he);
            float rat1 = wd / he;
            float rat2 = he / wd;
            if (wd > wr) {
                wd = wr;
                he = wd * rat2;
                Log.i("testings", "if (wd > wr) " + wd + "  " + he);
                if (he > hr) {
                    he = hr;
                    wd = he * rat1;
                    Log.i("testings", "  if (he > hr) " + wd + "  " + he);
                } else {
                    wd = wr;
                    he = wd * rat2;
                    Log.i("testings", " in else " + wd + "  " + he);
                }
            } else if (he > hr) {
                he = hr;
                wd = he * rat1;
                Log.i("testings", "  if (he > hr) " + wd + "  " + he);
                if (wd > wr) {
                    wd = wr;
                    he = wd * rat2;
                } else {
                    Log.i("testings", " in else " + wd + "  " + he);
                }
            } else if (rat1 > 0.75f) {
                wd = wr;
                he = wd * rat2;
                Log.i("testings", " if (rat1 > .75f) ");
            } else if (rat2 > 1.5f) {
                he = hr;
                wd = he * rat1;
                Log.i("testings", " if (rat2 > 1.5f) ");
            } else {
                he = wr * rat2;
                Log.i("testings", " in else ");
                if (he > hr) {
                    he = hr;
                    wd = he * rat1;
                    Log.i("testings", "  if (he > hr) " + wd + "  " + he);
                } else {
                    wd = wr;
                    he = wd * rat2;
                    Log.i("testings", " in else " + wd + "  " + he);
                }
            }
            return Bitmap.createScaledBitmap(bit, (int) wd, (int) he, false);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bit;
        } catch (Exception e2) {
            e2.printStackTrace();
            return bit;
        }
    }


}
