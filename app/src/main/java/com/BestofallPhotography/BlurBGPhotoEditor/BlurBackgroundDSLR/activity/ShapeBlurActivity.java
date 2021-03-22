package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;

import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.R;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.adapter.CustomCategoryAdapter;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.adapter.CustomShapeAdapter;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.customeView.TouchImageView_shape;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper.UserPermission;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper.mp4u;

public class ShapeBlurActivity extends Activity implements View.OnClickListener {
    private static Context mContext;
    public static TouchImageView_shape imageView;
    ImageView newBtn;
    TextView saveBtn;
    LinearLayout loader;
    LinearLayout blurView;
    TextView blurText;
    public static int wScreen;
    public static int hScreen;
    public static RecyclerView categoryRecyclerView;
    public static RecyclerView shapeRecyclerView;
    CustomCategoryAdapter categoryAdapter;
    public static CustomShapeAdapter shapeAdapter;
    public static LinearLayoutManager categoryLayoutManager;
    public static LinearLayoutManager shapeLayoutManager;
    public static CheckBox border;
    public static SeekBar blurrinessBar;
    private SimpleTarget gTarget;
    public static Bitmap bitmapBlur;
    public static Bitmap bitmapClear;
    public Shader shader1;
    public Shader shader2;
    File cameraImage;
    Uri cameraImageUri;
    float f18f;
    public static int f16h;
    public static int f17w;
    public Bitmap mask;
    public Bitmap maskContainer;
    String imageSavePath;
    public String userChoosenTask;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    public String photoFile;
    public String filename;
    boolean checkmemory = false;
    boolean isUpadted = false;

    public static int[] categoryID = new int[2];
    public static int[] selectedCategoryID = new int[2];
    public static int[][] shapeButtonID = new int[2][];
    public static int[][] shapeID = new int[2][];
    public static int[][] shapeViewID = new int[2][];
    public static int categoryIndex = 0;


    private AdView mAdView;
    InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setCategoryID();
        setContentView(R.layout.activity_shape_blur);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        if (mp4u.hasInternet(ShapeBlurActivity.this)) {
            mAdView.setVisibility(View.VISIBLE);
        } else {
            mAdView.setVisibility(View.GONE);
        }

        imageView = (TouchImageView_shape) findViewById(R.id.imageView);
        newBtn = (ImageView) findViewById(R.id.newBtn);
        saveBtn = (TextView) findViewById(R.id.saveBtn);
        newBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        loader = (LinearLayout) findViewById(R.id.loader);
        blurView = (LinearLayout) findViewById(R.id.blur_view);
        blurText = (TextView) findViewById(R.id.blur_text);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        wScreen = displayMetrics.widthPixels;
        hScreen = displayMetrics.heightPixels;

        gTarget = new SimpleTarget<Bitmap>(512, 512) {
            @Override
            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                imageView.last = new PointF(-5000.0f, -5000.0f);
                bitmapClear = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                f17w = bitmap.getWidth();
                f16h = bitmap.getHeight();
                int min = Math.min(f17w, f16h);
                if (min < 1024) {
                    f18f = ((float) min) / 1024.0f;
                }
                bitmapBlur = blur(getApplicationContext(), bitmapClear, imageView.opacity);
                imageView.setImageBitmap(ShapeBlurActivity.bitmapBlur);
                imageView.maskContainer = Bitmap.createBitmap(f17w, f16h, Bitmap.Config.ALPHA_8);
                imageView.hover = false;
                shapeAdapter.notifyDataSetChanged();
                imageView.init();
                imageView.lastPosIndex = -1;
                imageView.invalidate();
            }
        };
        String path = Environment.getExternalStorageDirectory().getPath();
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append(".jpg");
        cameraImage = new File(path, sb.toString());
        cameraImageUri = FileProvider.getUriForFile(this, "com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.provider", this.cameraImage);
        f18f = 1.0f;

        StringBuilder sb2 = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath()));
        sb2.append("/Blur Camera");
        imageSavePath = sb2.toString();

        categoryRecyclerView = (RecyclerView) findViewById(R.id.shapeCategory);
        shapeRecyclerView = (RecyclerView) findViewById(R.id.shapes);
        categoryRecyclerView.hasFixedSize();
        shapeRecyclerView.hasFixedSize();
        categoryAdapter = new CustomCategoryAdapter(this, categoryRecyclerView);
        shapeAdapter = new CustomShapeAdapter(this, shapeRecyclerView);
        categoryLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, true);
        categoryLayoutManager.setStackFromEnd(true);
        categoryRecyclerView.setLayoutManager(categoryLayoutManager);
        categoryRecyclerView.setAdapter(categoryAdapter);
        shapeLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, true);
        shapeLayoutManager.setStackFromEnd(true);
        shapeRecyclerView.setLayoutManager(shapeLayoutManager);
        shapeRecyclerView.setAdapter(shapeAdapter);
        border = (CheckBox) findViewById(R.id.border);
        border.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                imageView.invalidate();
            }
        });
        blurrinessBar = (SeekBar) findViewById(R.id.blurrinessSeekBar);
        blurrinessBar.setProgress(imageView.opacity);
        blurrinessBar.setMax(24);
        blurrinessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                imageView.opacity = i + 1;
                blurText.setText(new StringBuilder(String.valueOf(i)).toString());
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                blurView.setVisibility(View.VISIBLE);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                blurView.setVisibility(View.INVISIBLE);
                new BlurUpdater().execute(new String[0]);
            }
        });

        load();
    }

    public static Bitmap blur(Context context, Bitmap bitmap, int i) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap);
        Bitmap createBitmap2 = Bitmap.createBitmap(createBitmap);
        if (Build.VERSION.SDK_INT < 17) {
            return blurify(bitmap, i);
        }
        RenderScript create = RenderScript.create(context);
        ScriptIntrinsicBlur create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
        Allocation createFromBitmap = Allocation.createFromBitmap(create, createBitmap);
        Allocation createFromBitmap2 = Allocation.createFromBitmap(create, createBitmap2);
        create2.setRadius((float) i);
        create2.setInput(createFromBitmap);
        create2.forEach(createFromBitmap2);
        createFromBitmap2.copyTo(createBitmap2);
        return createBitmap2;
    }

    public static Bitmap blurify(Bitmap bitmap, int i) {
        int[] iArr;
        int i2 = i;
        Bitmap copy = bitmap.copy(bitmap.getConfig(), true);
        if (i2 < 1) {
            return null;
        }
        int width = copy.getWidth();
        int height = copy.getHeight();
        int i3 = width * height;
        int[] iArr2 = new int[i3];
        StringBuilder sb = new StringBuilder(String.valueOf(width));
        sb.append(" ");
        sb.append(height);
        sb.append(" ");
        sb.append(iArr2.length);
        Log.e("pix", sb.toString());
        copy.getPixels(iArr2, 0, width, 0, 0, width, height);
        int i4 = width - 1;
        int i5 = height - 1;
        int i6 = i2 + i2 + 1;
        int[] iArr3 = new int[i3];
        int[] iArr4 = new int[i3];
        int[] iArr5 = new int[i3];
        int[] iArr6 = new int[Math.max(width, height)];
        int i7 = (i6 + 1) >> 1;
        int i8 = i7 * i7;
        int i9 = i8 * 256;
        int[] iArr7 = new int[i9];
        for (int i10 = 0; i10 < i9; i10++) {
            iArr7[i10] = i10 / i8;
        }
        int[][] iArr8 = (int[][]) Array.newInstance(int.class, new int[]{i6, 3});
        int i11 = i2 + 1;
        int i12 = 0;
        int i13 = 0;
        int i14 = 0;
        while (i12 < height) {
            Bitmap bitmap2 = copy;
            int i15 = -i2;
            int i16 = 0;
            int i17 = 0;
            int i18 = 0;
            int i19 = 0;
            int i20 = 0;
            int i21 = 0;
            int i22 = 0;
            int i23 = 0;
            int i24 = 0;
            while (i15 <= i2) {
                int i25 = i5;
                int i26 = height;
                int i27 = iArr2[i13 + Math.min(i4, Math.max(i15, 0))];
                int[] iArr9 = iArr8[i15 + i2];
                iArr9[0] = (i27 & 16711680) >> 16;
                iArr9[1] = (i27 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                iArr9[2] = i27 & 255;
                int abs = i11 - Math.abs(i15);
                i16 += iArr9[0] * abs;
                i17 += iArr9[1] * abs;
                i18 += iArr9[2] * abs;
                if (i15 > 0) {
                    i19 += iArr9[0];
                    i20 += iArr9[1];
                    i21 += iArr9[2];
                } else {
                    i22 += iArr9[0];
                    i23 += iArr9[1];
                    i24 += iArr9[2];
                }
                i15++;
                height = i26;
                i5 = i25;
            }
            int i28 = i5;
            int i29 = height;
            int i30 = i2;
            int i31 = 0;
            while (i31 < width) {
                iArr3[i13] = iArr7[i16];
                iArr4[i13] = iArr7[i17];
                iArr5[i13] = iArr7[i18];
                int i32 = i16 - i22;
                int i33 = i17 - i23;
                int i34 = i18 - i24;
                int[] iArr10 = iArr8[((i30 - i2) + i6) % i6];
                int i35 = i22 - iArr10[0];
                int i36 = i23 - iArr10[1];
                int i37 = i24 - iArr10[2];
                if (i12 == 0) {
                    iArr = iArr7;
                    iArr6[i31] = Math.min(i31 + i2 + 1, i4);
                } else {
                    iArr = iArr7;
                }
                int i38 = iArr2[i14 + iArr6[i31]];
                iArr10[0] = (i38 & 16711680) >> 16;
                iArr10[1] = (i38 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                iArr10[2] = i38 & 255;
                int i39 = i19 + iArr10[0];
                int i40 = i20 + iArr10[1];
                int i41 = i21 + iArr10[2];
                i16 = i32 + i39;
                i17 = i33 + i40;
                i18 = i34 + i41;
                i30 = (i30 + 1) % i6;
                int[] iArr11 = iArr8[i30 % i6];
                i22 = i35 + iArr11[0];
                i23 = i36 + iArr11[1];
                i24 = i37 + iArr11[2];
                i19 = i39 - iArr11[0];
                i20 = i40 - iArr11[1];
                i21 = i41 - iArr11[2];
                i13++;
                i31++;
                iArr7 = iArr;
            }
            int[] iArr12 = iArr7;
            i14 += width;
            i12++;
            copy = bitmap2;
            height = i29;
            i5 = i28;
        }
        Bitmap bitmap3 = copy;
        int i42 = i5;
        int i43 = height;
        int[] iArr13 = iArr7;
        int i44 = 0;
        while (i44 < width) {
            int i45 = -i2;
            int i46 = i45 * width;
            int i47 = 0;
            int i48 = 0;
            int i49 = 0;
            int i50 = 0;
            int i51 = 0;
            int i52 = 0;
            int i53 = 0;
            int i54 = 0;
            int i55 = 0;
            while (i45 <= i2) {
                int[] iArr14 = iArr6;
                int max = Math.max(0, i46) + i44;
                int[] iArr15 = iArr8[i45 + i2];
                iArr15[0] = iArr3[max];
                iArr15[1] = iArr4[max];
                iArr15[2] = iArr5[max];
                int abs2 = i11 - Math.abs(i45);
                i47 += iArr3[max] * abs2;
                i48 += iArr4[max] * abs2;
                i49 += iArr5[max] * abs2;
                if (i45 > 0) {
                    i50 += iArr15[0];
                    i51 += iArr15[1];
                    i52 += iArr15[2];
                } else {
                    i53 += iArr15[0];
                    i54 += iArr15[1];
                    i55 += iArr15[2];
                }
                int i56 = i42;
                if (i45 < i56) {
                    i46 += width;
                }
                i45++;
                i42 = i56;
                iArr6 = iArr14;
            }
            int[] iArr16 = iArr6;
            int i57 = i42;
            int i58 = i44;
            int i59 = i51;
            int i60 = i52;
            int i61 = 0;
            int i62 = i2;
            int i63 = i50;
            int i64 = i49;
            int i65 = i48;
            int i66 = i47;
            int i67 = i43;
            while (i61 < i67) {
                iArr2[i58] = (iArr2[i58] & ViewCompat.MEASURED_STATE_MASK) | (iArr13[i66] << 16) | (iArr13[i65] << 8) | iArr13[i64];
                int i68 = i66 - i53;
                int i69 = i65 - i54;
                int i70 = i64 - i55;
                int[] iArr17 = iArr8[((i62 - i2) + i6) % i6];
                int i71 = i53 - iArr17[0];
                int i72 = i54 - iArr17[1];
                int i73 = i55 - iArr17[2];
                if (i44 == 0) {
                    iArr16[i61] = Math.min(i61 + i11, i57) * width;
                }
                int i74 = iArr16[i61] + i44;
                iArr17[0] = iArr3[i74];
                iArr17[1] = iArr4[i74];
                iArr17[2] = iArr5[i74];
                int i75 = i63 + iArr17[0];
                int i76 = i59 + iArr17[1];
                int i77 = i60 + iArr17[2];
                i66 = i68 + i75;
                i65 = i69 + i76;
                i64 = i70 + i77;
                i62 = (i62 + 1) % i6;
                int[] iArr18 = iArr8[i62];
                i53 = i71 + iArr18[0];
                i54 = i72 + iArr18[1];
                i55 = i73 + iArr18[2];
                i63 = i75 - iArr18[0];
                i59 = i76 - iArr18[1];
                i60 = i77 - iArr18[2];
                i58 += width;
                i61++;
                i2 = i;
            }
            i44++;
            i42 = i57;
            i43 = i67;
            iArr6 = iArr16;
            i2 = i;
        }
        int i78 = i43;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(width);
        sb2.append(" ");
        sb2.append(i78);
        sb2.append(" ");
        sb2.append(iArr2.length);
        Log.e("pix", sb2.toString());
        bitmap3.setPixels(iArr2, 0, width, 0, 0, width, i78);
        return bitmap3;
    }


    private void setCategoryID() {
        categoryID[0] = R.drawable.simple;
        categoryID[1] = R.drawable.complex;
        selectedCategoryID[0] = R.drawable.simple_selected;
        selectedCategoryID[1] = R.drawable.complex_selected;
        shapeButtonID[0] = new int[9];
        shapeID[0] = new int[9];
        shapeViewID[0] = new int[9];
        shapeButtonID[1] = new int[16];
        shapeID[1] = new int[16];
        shapeViewID[1] = new int[16];
        shapeButtonID[0][0] = R.drawable.simple1;
        shapeButtonID[0][1] = R.drawable.simple2;
        shapeButtonID[0][2] = R.drawable.simple3;
        shapeButtonID[0][3] = R.drawable.simple4;
        shapeButtonID[0][4] = R.drawable.simple5;
        shapeButtonID[0][5] = R.drawable.simple6;
        shapeButtonID[0][6] = R.drawable.simple7;
        shapeButtonID[0][7] = R.drawable.simple8;
        shapeButtonID[0][8] = R.drawable.simple9;
        shapeButtonID[1][0] = R.drawable.complex1;
        shapeButtonID[1][1] = R.drawable.complex2;
        shapeButtonID[1][2] = R.drawable.complex3;
        shapeButtonID[1][3] = R.drawable.complex4;
        shapeButtonID[1][4] = R.drawable.complex5;
        shapeButtonID[1][5] = R.drawable.complex6;
        shapeButtonID[1][6] = R.drawable.complex7;
        shapeButtonID[1][7] = R.drawable.complex8;
        shapeButtonID[1][8] = R.drawable.complex9;
        shapeButtonID[1][9] = R.drawable.complex10;
        shapeButtonID[1][10] = R.drawable.complex11;
        shapeButtonID[1][11] = R.drawable.complex12;
        shapeButtonID[1][12] = R.drawable.complex13;
        shapeButtonID[1][13] = R.drawable.complex14;
        shapeButtonID[1][14] = R.drawable.complex15;
        shapeButtonID[1][15] = R.drawable.complex16;
        shapeID[0][0] = R.drawable.a_1;
        shapeID[0][1] = R.drawable.a_3;
        shapeID[0][2] = R.drawable.a_4;
        shapeID[0][3] = R.drawable.a_2;
        shapeID[0][4] = R.drawable.a_5;
        shapeID[0][5] = R.drawable.a_6;
        shapeID[0][6] = R.drawable.a_7;
        shapeID[0][7] = R.drawable.a_9;
        shapeID[0][8] = R.drawable.a_22;
        shapeID[1][0] = R.drawable.a_11;
        shapeID[1][1] = R.drawable.a_8;
        shapeID[1][2] = R.drawable.a_25;
        shapeID[1][3] = R.drawable.a_10;
        shapeID[1][4] = R.drawable.a_14;
        shapeID[1][5] = R.drawable.a_12;
        shapeID[1][6] = R.drawable.a_13;
        shapeID[1][7] = R.drawable.a_15;
        shapeID[1][8] = R.drawable.a_17;
        shapeID[1][9] = R.drawable.a_24;
        shapeID[1][10] = R.drawable.a_16;
        shapeID[1][11] = R.drawable.a_18;
        shapeID[1][12] = R.drawable.a_19;
        shapeID[1][13] = R.drawable.a_20;
        shapeID[1][14] = R.drawable.a_23;
        shapeID[1][15] = R.drawable.a_21;
        shapeViewID[0][0] = R.raw.b_1;
        shapeViewID[0][1] = R.raw.b_3;
        shapeViewID[0][2] = R.raw.b_4;
        shapeViewID[0][3] = R.raw.b_2;
        shapeViewID[0][4] = R.raw.b_5;
        shapeViewID[0][5] = R.raw.b_6;
        shapeViewID[0][6] = R.raw.b_7;
        shapeViewID[0][7] = R.raw.b_9;
        shapeViewID[0][8] = R.raw.b_22;
        shapeViewID[1][0] = R.raw.b_11;
        shapeViewID[1][1] = R.raw.b_8;
        shapeViewID[1][2] = R.raw.b_25;
        shapeViewID[1][3] = R.raw.b_10;
        shapeViewID[1][4] = R.raw.b_14;
        shapeViewID[1][5] = R.raw.b_12;
        shapeViewID[1][6] = R.raw.b_13;
        shapeViewID[1][7] = R.raw.b_15;
        shapeViewID[1][8] = R.raw.b_17;
        shapeViewID[1][9] = R.raw.b_24;
        shapeViewID[1][10] = R.raw.b_16;
        shapeViewID[1][11] = R.raw.b_18;
        shapeViewID[1][12] = R.raw.b_19;
        shapeViewID[1][13] = R.raw.b_20;
        shapeViewID[1][14] = R.raw.b_23;
        shapeViewID[1][15] = R.raw.b_21;
        reverseArray(shapeButtonID[0]);
        reverseArray(shapeButtonID[1]);
        reverseArray(shapeID[0]);
        reverseArray(shapeViewID[0]);
        reverseArray(shapeID[1]);
        reverseArray(shapeViewID[1]);
        reverseArray(categoryID);
        reverseArray(selectedCategoryID);
    }

    public void reverseArray(int[] iArr) {
        for (int i = 0; i < iArr.length / 2; i++) {
            int i2 = iArr[i];
            iArr[i] = iArr[(iArr.length - i) - 1];
            iArr[(iArr.length - i) - 1] = i2;
        }
    }


    private class BlurUpdater extends AsyncTask<String, Integer, String> {
        private BlurUpdater() {
        }

        public void onPreExecute() {
            super.onPreExecute();
            ShapeBlurActivity.this.loader.setVisibility(View.VISIBLE);
        }

        public String doInBackground(String... strArr) {
            ShapeBlurActivity.bitmapBlur = ShapeBlurActivity.blur(ShapeBlurActivity.this.getApplicationContext(), ShapeBlurActivity.bitmapClear, ShapeBlurActivity.imageView.opacity);
            return "this string is passed to onPostExecute";
        }


        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
        }


        public void onPostExecute(String str) {
            super.onPostExecute(str);
            imageView.shader2 = new BitmapShader(ShapeBlurActivity.bitmapBlur, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            if (ShapeBlurActivity.imageView.bool) {
                ShapeBlurActivity.imageView.paint.setShader(ShapeBlurActivity.imageView.shader1);
                ShapeBlurActivity.imageView.setImageBitmap(ShapeBlurActivity.bitmapBlur);
            } else {
                ShapeBlurActivity.imageView.paint.setShader(ShapeBlurActivity.imageView.shader2);
                ShapeBlurActivity.imageView.setImageBitmap(ShapeBlurActivity.bitmapClear);
            }
            ShapeBlurActivity.imageView.invalidate();
            ShapeBlurActivity.this.loader.setVisibility(View.INVISIBLE);
        }
    }

    public void load() {
        Glide.with((Activity) this).asBitmap().load(Integer.valueOf(R.drawable.me)).into(gTarget);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.newBtn) {
            selectImage();
        } else if (id == R.id.saveBtn) {
            saveBitmap(true);

        }
    }

    private void selectImage() {
        final CharSequence[] charSequenceArr = {"Choose from Gallery", "Take Photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialog);
        builder.setTitle("Add Photo!");
        builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean checkPermission = UserPermission.checkPermission(ShapeBlurActivity.this);
                if (charSequenceArr[i].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (checkPermission) {
                        cameraIntent();
                    }
                } else if (charSequenceArr[i].equals("Choose from Gallery")) {
                    ShapeBlurActivity.this.userChoosenTask = "Choose from Gallery";
                    if (checkPermission) {
                        galleryIntent();
                    }
                } else if (charSequenceArr[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    public void cameraIntent() {
        String path = Environment.getExternalStorageDirectory().getPath();
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append(".jpg");
        this.cameraImage = new File(path, sb.toString());
        this.cameraImageUri = FileProvider.getUriForFile(this, "com.BestofallPhotography.app.BlurBackgroundDSLR.provider", this.cameraImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", this.cameraImageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    public void saveBitmap(final boolean z) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.plzwait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    File file = new File(mp4u.newDir.toString());
                    if (!file.exists()) {
                        if (!file.mkdirs()) {
                            Log.d("", "Can't create directory to save image.");
                            Toast.makeText(ShapeBlurActivity.this.getApplicationContext(), ShapeBlurActivity.this.getResources().getString(R.string.create_dir_err), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    ShapeBlurActivity shapeBlurActivity = ShapeBlurActivity.this;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Photo_");
                    sb2.append(System.currentTimeMillis());
                    shapeBlurActivity.photoFile = sb2.toString();
                    if (z) {
                        ShapeBlurActivity shapeBlurActivity2 = ShapeBlurActivity.this;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(ShapeBlurActivity.this.photoFile);
                        sb3.append(".png");
                        shapeBlurActivity2.photoFile = sb3.toString();
                    } else {
                        ShapeBlurActivity shapeBlurActivity3 = ShapeBlurActivity.this;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append(ShapeBlurActivity.this.photoFile);
                        sb4.append(".jpg");
                        shapeBlurActivity3.photoFile = sb4.toString();
                    }
                    ShapeBlurActivity shapeBlurActivity4 = ShapeBlurActivity.this;
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append(file.getPath());
                    sb5.append(File.separator);
                    sb5.append(ShapeBlurActivity.this.photoFile);
                    shapeBlurActivity4.filename = sb5.toString();
                    File file2 = new File(ShapeBlurActivity.this.filename);
                    try {
                        if (!file2.exists()) {
                            file2.createNewFile();
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(file2);
                        if (z) {
                            ShapeBlurActivity.this.checkmemory = ShapeBlurActivity.generateOutputBitmap(ShapeBlurActivity.this.getApplicationContext()).compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        } else {
                            Bitmap createBitmap = Bitmap.createBitmap(ShapeBlurActivity.generateOutputBitmap(ShapeBlurActivity.this.getApplicationContext()).getWidth(), ShapeBlurActivity.generateOutputBitmap(ShapeBlurActivity.this.getApplicationContext()).getHeight(), ShapeBlurActivity.generateOutputBitmap(ShapeBlurActivity.this.getApplicationContext()).getConfig());
                            Canvas canvas = new Canvas(createBitmap);
                            canvas.drawColor(-1);
                            canvas.drawBitmap(ShapeBlurActivity.generateOutputBitmap(ShapeBlurActivity.this.getApplicationContext()), 0.0f, 0.0f, null);
                            ShapeBlurActivity.this.checkmemory = createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            createBitmap.recycle();
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        ShapeBlurActivity.this.isUpadted = true;
                        ShapeBlurActivity.this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file2)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(1000);
                    progressDialog.dismiss();
                    Intent intent = new Intent(ShapeBlurActivity.this.getApplicationContext(), ShareActivity.class);
                    intent.putExtra("path", ShapeBlurActivity.this.filename);
                    ShapeBlurActivity.this.startActivity(intent);
                } catch (Exception unused) {
                }
            }
        }).start();
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                boolean z = ShapeBlurActivity.this.checkmemory;
            }
        });
    }

    static Bitmap generateOutputBitmap(Context context) {
        Bitmap bitmap;
        float f;
        if (imageView.bool) {
            bitmap = bitmapBlur.copy(Bitmap.Config.ARGB_8888, true);
        } else {
            bitmap = bitmapClear.copy(Bitmap.Config.ARGB_8888, true);
        }
        Canvas canvas = new Canvas(bitmap);
        imageView.maskContainer = Bitmap.createBitmap(f17w, f16h, Bitmap.Config.ALPHA_8);
        Canvas canvas2 = new Canvas(imageView.maskContainer);
        Matrix matrix = new Matrix();
        if (f17w > f16h) {
            f = ((float) f17w) / ((float) wScreen);
        } else {
            f = ((float) f16h) / ((float) imageView.getHeight());
        }
        matrix.setScale(f, f);
        matrix.postRotate(imageView.mRotationDegree, (((float) imageView.wMask) * f) / 2.0f, (((float) imageView.wMask) * f) / 2.0f);
        matrix.postTranslate(((imageView.last.x - imageView.f19m[2]) / imageView.f19m[0]) - ((((float) imageView.wMask) * f) / 2.0f), ((imageView.last.y - imageView.f19m[5]) / imageView.f19m[4]) - ((((float) imageView.wMask) * f) / 2.0f));
        canvas2.drawBitmap(imageView.mask, matrix, null);
        canvas.drawBitmap(imageView.maskContainer, 0.0f, 0.0f, imageView.paint);
        if (border.isChecked()) {
            imageView.preview = imageView.svgToBitmap(imageView.getResources(), imageView.svgId, (int) (((float) imageView.wMask) * f));
            Matrix matrix2 = new Matrix();
            matrix2.postTranslate(((imageView.last.x - imageView.f19m[2]) / imageView.f19m[0]) - ((((float) imageView.wMask) * f) / 2.0f), ((imageView.last.y - imageView.f19m[5]) / imageView.f19m[4]) - ((((float) imageView.wMask) * f) / 2.0f));
            canvas.drawBitmap(imageView.preview, matrix2, null);
        }
        return bitmap;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mInterstitialAd != null) {
            mInterstitialAd.setAdListener(null);
        }
    }
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 != -1) {
            return;
        }
        if (i == this.SELECT_FILE) {
            onSelectFromGalleryResult(intent);
        } else if (i == this.REQUEST_CAMERA) {
            onCaptureImageResult(intent);
        }
    }
    private void onCaptureImageResult(Intent intent) {
        Glide.with((Activity) this).asBitmap().load(cameraImageUri).into(gTarget);

    }

    private void onSelectFromGalleryResult(Intent intent) {
        Glide.with((Activity) this).asBitmap().load(intent.getData()).into(gTarget);

    }
}
