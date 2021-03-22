package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;

import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.R;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.customeView.TouchImageView;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.customeView.BrushView;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper.UserPermission;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper.mp4u;


public class BlurActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    public static SeekBar offsetBar;
    public static ImageView prView;
    public static Bitmap bitmapBlur;
    public static Bitmap bitmapClear;
    public static int displayHight;
    public static int displayWidth;
    public static BrushView brushView;
    public static SeekBar radiusBar;
    public static File tempDrawPathFile;

    Bitmap hand;
    LinearLayout blurView;
    public static TouchImageView tiv;
    TextView blurText;
    LinearLayout bluringImg;
    LinearLayout adsLayout;
    LinearLayout newBtn;
    LinearLayout offsetBtn;
    ImageView offsetDemo;
    LinearLayout offsetLayout;
    TextView offsetOk;
    public String photoFile;
    ProgressBar progressBar;
    ProgressDialog progressBlurring;
    LinearLayout resetBtn;
    TextView saveBtn;
    int startBlurSeekbarPosition;
    LinearLayout tabPic;
    LinearLayout undoBtn;
    LinearLayout fitBtn;
    LinearLayout clearRazing;
    static SeekBar blurrinessBar;
    String imageSavePath;
    static String tempDrawPath;
    int btnbgColor = 1644825;
    int btnbgColorCurrent;
    boolean erase;
    public String userChoosenTask;
    File cameraImage;
    Uri cameraImageUri;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private SimpleTarget gTarget;
    boolean checkmemory = false;
    public String filename;
    boolean isUpadted;

    static {
        StringBuilder sb = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath()));
        sb.append("/Blur Camera");
        tempDrawPath = sb.toString();
    }

    private AdView mAdView;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        displayWidth = point.x;
        displayHight = point.y;
        setContentView(R.layout.activity_blur);


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        if (mp4u.hasInternet(BlurActivity.this)) {
            mAdView.setVisibility(View.VISIBLE);
        } else {
            mAdView.setVisibility(View.GONE);
        }

        hand = BitmapFactory.decodeResource(getResources(), R.drawable.hand);
        hand = Bitmap.createScaledBitmap(this.hand, 120, 120, true);
        blurView = (LinearLayout) findViewById(R.id.blur_view);
        blurText = (TextView) findViewById(R.id.blur_text);
        tiv = (TouchImageView) findViewById(R.id.drawingImageView);
        prView = (ImageView) findViewById(R.id.preview);
        // adsLayout = (LinearLayout) findViewById(R.id.ads_layout);
        offsetDemo = (ImageView) findViewById(R.id.offsetDemo);
        offsetLayout = (LinearLayout) findViewById(R.id.offsetLayout);
        //  progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bitmapClear = BitmapFactory.decodeResource(getResources(), R.drawable.me);
        bitmapBlur = blur(this, bitmapClear, tiv.opacity);
        newBtn = (LinearLayout) findViewById(R.id.newBtn);
        resetBtn = (LinearLayout) findViewById(R.id.resetBtn);
        undoBtn = (LinearLayout) findViewById(R.id.undoBtn);
        fitBtn = (LinearLayout) findViewById(R.id.fitBtn);
        saveBtn = (TextView) findViewById(R.id.saveBtn);
        offsetBtn = (LinearLayout) findViewById(R.id.offsetBtn);
        clearRazing = (LinearLayout) findViewById(R.id.clear_razing);
        bluringImg = (LinearLayout) findViewById(R.id.bluring_image);
        tabPic = (LinearLayout) findViewById(R.id.tap_pic);
        offsetOk = (TextView) findViewById(R.id.offsetOk);

        String path = Environment.getExternalStorageDirectory().getPath();
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append(".jpg");
        cameraImage = new File(path, sb.toString());
        cameraImageUri = FileProvider.getUriForFile(this, "com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.provider", this.cameraImage);
        isUpadted = false;
        erase = true;
        gTarget = new SimpleTarget<Bitmap>(512, 512) {
            @Override
            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                bitmapClear = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                bitmapBlur = blur(getApplicationContext(), bitmapClear, tiv.opacity);
                clearTempBitmap();
                tiv.initDrawing();
                tiv.saveScale = 1.0f;
                tiv.fitScreen();
                tiv.updatePreviewPaint();
                tiv.updatePaintBrush();
                bluringImg.setBackgroundColor(btnbgColor);
                tabPic.setBackgroundColor(btnbgColor);
                clearRazing.setBackgroundColor(btnbgColorCurrent);
            }


        };
        StringBuilder sb2 = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath()));
        sb2.append("/Blur Camera");
        imageSavePath = sb2.toString();

        newBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        undoBtn.setOnClickListener(this);
        this.fitBtn.setOnClickListener(this);
        this.saveBtn.setOnClickListener(this);
        this.clearRazing.setOnClickListener(this);
        this.bluringImg.setOnClickListener(this);
        this.tabPic.setOnClickListener(this);
        this.offsetBtn.setOnClickListener(this);
        this.offsetOk.setOnClickListener(this);
        offsetBar = (SeekBar) findViewById(R.id.offsetBar);
        radiusBar = (SeekBar) findViewById(R.id.widthSeekBar);
        blurrinessBar = (SeekBar) findViewById(R.id.blurrinessSeekBar);
        brushView = (BrushView) findViewById(R.id.magnifyingView);
        brushView.setShapeRadiusRatio(((float) radiusBar.getProgress()) / ((float) radiusBar.getMax()));
        radiusBar.setMax(300);
        radiusBar.setProgress((int) tiv.radius);
        blurrinessBar.setMax(24);
        blurrinessBar.setProgress(tiv.opacity);
        offsetBar.setMax(100);
        offsetBar.setProgress(0);
        radiusBar.setOnSeekBarChangeListener(this);
        blurrinessBar.setOnSeekBarChangeListener(this);
        offsetBar.setOnSeekBarChangeListener(this);
        File file = new File(this.imageSavePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        clearTempBitmap();
        tiv.initDrawing();
        this.progressBlurring = new ProgressDialog(this);

        CustomDialog customDialog = new CustomDialog(this);
        if (PreferenceManager.getDefaultSharedPreferences(this).getString("show", "yes").equals("yes")) {
            customDialog.show();
        }
        this.btnbgColorCurrent = getResources().getColor(R.color.color_bg);


    }


    public static Bitmap blur(Context context, Bitmap bitmap2, int i) {
        Bitmap copy = bitmap2.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap createBitmap = Bitmap.createBitmap(copy);
        if (Build.VERSION.SDK_INT < 17) {
            return blurify(copy, i);
        }
        RenderScript create = RenderScript.create(context);
        ScriptIntrinsicBlur create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
        Allocation createFromBitmap = Allocation.createFromBitmap(create, copy);
        Allocation createFromBitmap2 = Allocation.createFromBitmap(create, createBitmap);
        create2.setRadius((float) i);
        create2.setInput(createFromBitmap);
        create2.forEach(createFromBitmap2);
        createFromBitmap2.copyTo(createBitmap);
        return createBitmap;
    }

    public static Bitmap blurify(Bitmap bitmap2, int i) {
        int[] iArr;
        int i2 = i;
        Bitmap copy = bitmap2.copy(bitmap2.getConfig(), true);
        if (i2 < 1) {
            return null;
        }
        int width = copy.getWidth();
        int height = copy.getHeight();
        int i3 = width * height;
        int[] iArr2 = new int[i3];
        StringBuilder sb = new StringBuilder();
        sb.append(width);
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
            Bitmap bitmap3 = copy;
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
            copy = bitmap3;
            height = i29;
            i5 = i28;
        }
        Bitmap bitmap4 = copy;
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
        bitmap4.setPixels(iArr2, 0, width, 0, 0, width, i78);
        return bitmap4;
    }

    public void clearTempBitmap() {
        tempDrawPathFile = new File(tempDrawPath);
        if (!tempDrawPathFile.exists()) {
            tempDrawPathFile.mkdirs();
        }
        if (tempDrawPathFile.isDirectory()) {
            for (String file : tempDrawPathFile.list()) {
                new File(tempDrawPathFile, file).delete();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bluring_image :
                erase = false;
                TouchImageView touchImageView = tiv;
                TouchImageView touchImageView2 = tiv;
                touchImageView.mode = 0;
                this.clearRazing.setBackgroundColor(this.btnbgColor);
                this.tabPic.setBackgroundColor(this.btnbgColor);
                this.bluringImg.setBackgroundColor(this.btnbgColorCurrent);
                tiv.splashBitmap = bitmapBlur;
                tiv.updateRefMetrix();
                tiv.changeShaderBitmap();
                tiv.coloring = false;
                return;
            case R.id.clear_razing :
                this.erase = true;
                TouchImageView touchImageView3 = tiv;
                TouchImageView touchImageView4 = tiv;
                touchImageView3.mode = 0;
                this.bluringImg.setBackgroundColor(this.btnbgColor);
                this.tabPic.setBackgroundColor(this.btnbgColor);
                this.clearRazing.setBackgroundColor(this.btnbgColorCurrent);
                tiv.splashBitmap = bitmapClear;
                tiv.updateRefMetrix();
                tiv.changeShaderBitmap();
                tiv.coloring = true;
                return;
            case R.id.fitBtn :
                tiv.saveScale = 1.0f;
                tiv.radius = ((float) (radiusBar.getProgress() + 50)) / tiv.saveScale;
                brushView.setShapeRadiusRatio(((float) (radiusBar.getProgress() + 50)) / tiv.saveScale);
                tiv.fitScreen();
                tiv.updatePreviewPaint();
                return;
            case R.id.newBtn :
                selectImage();

                return;
            case R.id.offsetBtn :
                if (this.offsetLayout.getVisibility() == View.VISIBLE) {
                    this.offsetLayout.setVisibility(View.GONE);
                    this.offsetBtn.setBackgroundColor(0);
                    return;
                }
                this.offsetLayout.setVisibility(View.VISIBLE);
                this.offsetBtn.setBackgroundColor(Color.parseColor("#E7D7D7D7"));
                return;
            case R.id.offsetOk :
                this.offsetLayout.setVisibility(View.INVISIBLE);
                this.offsetBtn.setBackgroundColor(0);
                return;
            case R.id.resetBtn :
                resetImage();
                return;
            case R.id.saveBtn :
                saveBitmap(true);
                return;
            case R.id.tap_pic :
                TouchImageView touchImageView5 = tiv;
                TouchImageView touchImageView6 = tiv;
                touchImageView5.mode = 1;
                this.bluringImg.setBackgroundColor(this.btnbgColor);
                this.clearRazing.setBackgroundColor(this.btnbgColor);
                this.tabPic.setBackgroundColor(this.btnbgColorCurrent);
                return;
            case R.id.undoBtn :
                StringBuilder sb = new StringBuilder();
                sb.append(tempDrawPath);
                sb.append("/canvasLog");
                sb.append(tiv.currentImageIndex - 1);
                sb.append(".jpg");
                String sb2 = sb.toString();
                Log.wtf("Current Image ", sb2);
                if (new File(sb2).exists()) {
                    tiv.drawingBitmap = null;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    options.inMutable = true;
                    tiv.drawingBitmap = BitmapFactory.decodeFile(sb2, options);
                    tiv.setImageBitmap(tiv.drawingBitmap);
                    tiv.canvas.setBitmap(tiv.drawingBitmap);
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(tempDrawPath);
                    sb3.append("canvasLog");
                    sb3.append(tiv.currentImageIndex);
                    sb3.append(".jpg");
                    File file = new File(sb3.toString());
                    if (file.exists()) {
                        file.delete();
                    }
                    TouchImageView touchImageView7 = tiv;
                    touchImageView7.currentImageIndex--;
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void selectImage() {
        final CharSequence[] charSequenceArr = {"Choose from Gallery", "Take Photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialog);
        builder.setTitle("Add Photo!");
        builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean checkPermission = UserPermission.checkPermission(BlurActivity.this);
                if (charSequenceArr[i].equals("Take Photo")) {
                    BlurActivity.this.userChoosenTask = "Take Photo";
                    if (checkPermission) {
                        cameraIntent();
                    }
                } else if (charSequenceArr[i].equals("Choose from Gallery")) {
                    BlurActivity.this.userChoosenTask = "Choose from Gallery";
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

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i != 123 || iArr.length <= 0 || iArr[0] != 0) {
            return;
        }
        if (this.userChoosenTask.equals("Take Photo")) {
            cameraIntent();
        } else if (this.userChoosenTask.equals("Choose from Gallery")) {
            galleryIntent();
        }
    }

    public void cameraIntent() {
        String path = Environment.getExternalStorageDirectory().getPath();
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append(".jpg");
        cameraImage = new File(path, sb.toString());
        cameraImageUri = FileProvider.getUriForFile(this, "com.BestofallPhotography.app.BlurBackgroundDSLR.provider", this.cameraImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", this.cameraImageUri);
        startActivityForResult(intent, this.REQUEST_CAMERA);
    }

    public void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select File"), this.SELECT_FILE);
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
        // Glide.with(BlurActivity.this).load(intent.getData()).into(gTarget);
    }

    public void resetImage() {
        AlertDialog create = new AlertDialog.Builder(this, R.style.alertDialogreset).create();
        create.setMessage("Your current progress will be lost. Are you sure?");
        create.setButton(-1, "Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                clearTempBitmap();
                tiv.initDrawing();
                tiv.saveScale = 1.0f;
                tiv.fitScreen();
                tiv.updatePreviewPaint();
                tiv.updatePaintBrush();
            }
        });
        create.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        create.show();
        create.getButton(-2).setTextColor(getResources().getColor(R.color.bursh_ok_but));
        create.getButton(-1).setTextColor(getResources().getColor(R.color.bursh_ok_but));
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
                            Toast.makeText(BlurActivity.this.getApplicationContext(), getResources().getString(R.string.create_dir_err), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    BlurActivity blurActivity = BlurActivity.this;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Photo_");
                    sb2.append(System.currentTimeMillis());
                    blurActivity.photoFile = sb2.toString();
                    if (z) {
                        BlurActivity mainActivity2 = BlurActivity.this;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(BlurActivity.this.photoFile);
                        sb3.append(".png");
                        mainActivity2.photoFile = sb3.toString();
                    } else {
                        BlurActivity mainActivity3 = BlurActivity.this;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append(BlurActivity.this.photoFile);
                        sb4.append(".jpg");
                        mainActivity3.photoFile = sb4.toString();
                    }
                    BlurActivity mainActivity4 = BlurActivity.this;
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append(file.getPath());
                    sb5.append(File.separator);
                    sb5.append(BlurActivity.this.photoFile);
                    mainActivity4.filename = sb5.toString();
                    File file2 = new File(filename);
                    try {
                        if (!file2.exists()) {
                            file2.createNewFile();
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(file2);
                        if (z) {
                            checkmemory = tiv.drawingBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        } else {
                            Bitmap createBitmap = Bitmap.createBitmap(tiv.drawingBitmap.getWidth(), tiv.drawingBitmap.getHeight(), tiv.drawingBitmap.getConfig());
                            Canvas canvas = new Canvas(createBitmap);
                            canvas.drawColor(-1);
                            canvas.drawBitmap(tiv.drawingBitmap, 0.0f, 0.0f, null);
                            checkmemory = createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            createBitmap.recycle();
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        isUpadted = true;
                        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file2)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(1000);
                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
                    intent.putExtra("path", filename);
                    startActivity(intent);
                } catch (Exception unused) {
                }
            }
        }).start();
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                boolean z = checkmemory;
            }
        });
    }

    class CustomDialog extends Dialog {
        Context ctx;

        public CustomDialog(Context context) {
            super(context);
            this.ctx = context;
        }

        public CustomDialog(Context context, int i) {
            super(context, i);
            this.ctx = context;
        }

        protected CustomDialog(Context context, boolean z, OnCancelListener onCancelListener) {
            super(context, z, onCancelListener);
            this.ctx = context;
        }

        public void show() {
            requestWindowFeature(1);
            View inflate = LayoutInflater.from(this.ctx).inflate(R.layout.popup_tip, null);
            setCanceledOnTouchOutside(false);
            setCancelable(false);
            setContentView(inflate);
            //  ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams();
            //  layoutParams.copyFrom(getWindow().getAttributes());
            //  layoutParams.width = -1;
            //  layoutParams.height = -2;
            //  layoutParams.gravity = 17;
            //  getWindow().setAttributes(layoutParams);
            final CheckBox checkBox = (CheckBox) inflate.findViewById(R.id.dont_show);
            findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (checkBox.isChecked()) {
                        PreferenceManager.getDefaultSharedPreferences(BlurActivity.this).edit().putString("show", "no").commit();
                    } else {
                        PreferenceManager.getDefaultSharedPreferences(BlurActivity.this).edit().putString("show", "yes").commit();
                    }
                    CustomDialog.this.dismiss();
                }
            });
            super.show();
        }
    }


    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        int id = seekBar.getId();
        if (id == R.id.blurrinessSeekBar) {
            brushView.isBrushSize = false;
            brushView.setShapeRadiusRatio(tiv.radius);
            brushView.brushSize.setPaintOpacity(blurrinessBar.getProgress());
            brushView.invalidate();
            tiv.opacity = i + 1;
            this.blurText.setText(new StringBuilder(String.valueOf(blurrinessBar.getProgress())).toString());
            tiv.updatePaintBrush();
        } else if (id == R.id.offsetBar) {
            Bitmap copy = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(copy);
            Paint paint = new Paint(1);
            paint.setColor(-16711936);
            canvas.drawCircle(150.0f, (float) (150 - offsetBar.getProgress()), 30.0f, paint);
            canvas.drawBitmap(this.hand, 95.0f, 150.0f, null);
            this.offsetDemo.setImageBitmap(copy);
        } else if (id == R.id.widthSeekBar) {
            brushView.isBrushSize = true;
            brushView.brushSize.setPaintOpacity(255);
            brushView.setShapeRadiusRatio(((float) (radiusBar.getProgress() + 50)) / tiv.saveScale);
            Log.wtf("radious :", new StringBuilder(String.valueOf(radiusBar.getProgress())).toString());
            brushView.invalidate();
            tiv.radius = ((float) (radiusBar.getProgress() + 50)) / tiv.saveScale;
            tiv.updatePaintBrush();
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        int id = seekBar.getId();
        if (id == R.id.blurrinessSeekBar) {
            this.blurView.setVisibility(View.VISIBLE);
            this.startBlurSeekbarPosition = blurrinessBar.getProgress();
            this.blurText.setText(new StringBuilder(String.valueOf(this.startBlurSeekbarPosition)).toString());
        } else if (id == R.id.offsetBar) {
            this.offsetDemo.setVisibility(View.VISIBLE);
            Bitmap copy = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(copy);
            Paint paint = new Paint(1);
            paint.setColor(-16711936);
            canvas.drawCircle(150.0f, (float) (150 - offsetBar.getProgress()), 30.0f, paint);
            canvas.drawBitmap(this.hand, 95.0f, 150.0f, null);
            this.offsetDemo.setImageBitmap(copy);
        } else if (id == R.id.widthSeekBar) {
            brushView.setVisibility(View.VISIBLE);
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        this.blurView.setVisibility(View.INVISIBLE);
        if (seekBar.getId() == R.id.blurrinessSeekBar) {
            AlertDialog create = new AlertDialog.Builder(this, R.style.alertDialogreset).create();
            create.setTitle("Warning");
            create.setMessage("Changing Bluriness will lose your current drawing progress!");
            create.setButton(-1, "Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    new BlurUpdater().execute(new String[0]);
                    clearRazing.setBackgroundColor(btnbgColorCurrent);
                    bluringImg.setBackgroundColor(btnbgColor);
                }
            });
            create.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    blurrinessBar.setProgress(startBlurSeekbarPosition);
                }
            });
            create.show();
        } else if (seekBar.getId() == R.id.offsetBar) {
            this.offsetDemo.setVisibility(View.INVISIBLE);
        } else if (seekBar.getId() == R.id.widthSeekBar) {
            brushView.setVisibility(View.INVISIBLE);
        }
    }

    private class BlurUpdater extends AsyncTask<String, Integer, Bitmap> {
        private BlurUpdater() {
        }


        public void onPreExecute() {
            super.onPreExecute();
            progressBlurring.setMessage("Blurring...");
            progressBlurring.setIndeterminate(true);
            progressBlurring.setCancelable(false);
            progressBlurring.show();
        }


        public Bitmap doInBackground(String... strArr) {
            bitmapBlur = blur(getApplicationContext(), bitmapClear, tiv.opacity);
            return bitmapBlur;
        }


        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
        }


        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (!erase) {
                tiv.splashBitmap = bitmapBlur;
                tiv.updateRefMetrix();
                tiv.changeShaderBitmap();
            }
            clearTempBitmap();
            tiv.initDrawing();
            tiv.saveScale = 1.0f;
            tiv.fitScreen();
            tiv.updatePreviewPaint();
            tiv.updatePaintBrush();
            if (progressBlurring.isShowing()) {
                progressBlurring.dismiss();
            }
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