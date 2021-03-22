package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.customeView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.caverock.androidsvg.SVG;

import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.R;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.activity.ShapeBlurActivity;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.gesture.RotateGestureDetector;


public class TouchImageView_shape extends AppCompatImageView {
    public boolean bool = true;
    public Canvas canvasMask;
    Context context;
    float density;
    public float[] f19m;
    float factor;
    public Bitmap finalBitmap;
    public Canvas finalCanvas;
    public boolean hover = false;
    public PointF initialShapePos = new PointF(-5000.0f, -5000.0f);
    public PointF last = new PointF(-5000.0f, -5000.0f);
    public int lastCatIndex = 0;
    public int lastPosIndex = 6;
    RotateGestureDetector mRotateDetector;
    public float mRotationDegree = 0.0f;
    ScaleGestureDetector mScaleDetector;
    public Bitmap mask;
    public Bitmap maskContainer;
    Matrix mat;
    Matrix matrix;
    int oldMeasuredHeight;
    int oldMeasuredWidth;
    public int opacity = 25;
    protected float origHeight;
    protected float origWidth;
    public Paint paint;
    public Bitmap preview;
    Rect rect;
    float saveScale = 1.0f;
    public Shader shader1;
    public Shader shader2;
    public Bitmap spot;
    PointF start;
    public int svgId = R.raw.b_7;
    public Canvas temp;
    int viewHeight;
    int viewWidth;
    public int wMask;


    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        private RotateListener() {
        }

        public boolean onRotate(RotateGestureDetector rotateGestureDetector) {
            mRotationDegree -= rotateGestureDetector.getRotationDegreesDelta();
            return true;
        }
    }


    private class ScaleListener extends SimpleOnScaleGestureListener {
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        }

        private ScaleListener() {
        }

        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            TouchImageView_shape touchImageView = TouchImageView_shape.this;
            touchImageView.wMask = (int) (((float) touchImageView.wMask) * scaleFactor);
            if (TouchImageView_shape.this.wMask < 100) {
                TouchImageView_shape.this.wMask = 100;
            } else if (TouchImageView_shape.this.wMask > 1200) {
                TouchImageView_shape.this.wMask = 1200;
            }
            TouchImageView_shape.this.mask = Bitmap.createScaledBitmap(TouchImageView_shape.this.spot, TouchImageView_shape.this.wMask, TouchImageView_shape.this.wMask, true);
            return true;
        }
    }


    public float getFixTrans(float f, float f2, float f3) {
        float f4;
        float f5;
        if (f3 <= f2) {
            f4 = f2 - f3;
            f5 = 0.0f;
        } else {
            f5 = f2 - f3;
            f4 = 0.0f;
        }
        if (f < f5) {
            return (-f) + f5;
        }
        if (f > f4) {
            return (-f) + f4;
        }
        return 0.0f;
    }

    public TouchImageView_shape(Context context2) {
        super(context2);
        sharedConstructing(context2);
        setDrawingCacheEnabled(true);
    }

    public TouchImageView_shape(Context context2, AttributeSet attributeSet) {
        super(context2, attributeSet);
        sharedConstructing(context2);
        setDrawingCacheEnabled(true);
    }

    public void init() {
        this.shader1 = new BitmapShader(ShapeBlurActivity.bitmapClear, TileMode.CLAMP, TileMode.CLAMP);
        this.shader2 = new BitmapShader(ShapeBlurActivity.bitmapBlur, TileMode.CLAMP, TileMode.CLAMP);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setShader(this.shader1);
        if (this.lastPosIndex != -1) {
            this.mask = BitmapFactory.decodeResource(getResources(), ShapeBlurActivity.shapeID[this.lastCatIndex][this.lastPosIndex]).copy(Config.ALPHA_8, true);
            this.spot = BitmapFactory.decodeResource(getResources(), ShapeBlurActivity.shapeID[this.lastCatIndex][this.lastPosIndex]).copy(Config.ALPHA_8, true);
        } else {
            this.mask = BitmapFactory.decodeResource(getResources(), ShapeBlurActivity.shapeID[this.lastCatIndex][0]).copy(Config.ALPHA_8, true);
            this.spot = BitmapFactory.decodeResource(getResources(), ShapeBlurActivity.shapeID[this.lastCatIndex][0]).copy(Config.ALPHA_8, true);
        }
        this.wMask = this.mask.getWidth();
        this.canvasMask = new Canvas(this.maskContainer);
        this.mRotationDegree = 0.0f;
        this.bool = true;
        this.finalCanvas = new Canvas();
        this.temp = new Canvas();
        this.mat = new Matrix();
        if (ShapeBlurActivity.f17w > ShapeBlurActivity.f16h) {
            this.factor = ((float) ShapeBlurActivity.f17w) / ((float) ShapeBlurActivity.wScreen);
        } else {
            this.factor = ((float) ShapeBlurActivity.f16h) / ((float) ShapeBlurActivity.imageView.getHeight());
        }
    }

    private void sharedConstructing(Context context2) {
        super.setClickable(true);
        this.context = context2;
        this.density = context2.getResources().getDisplayMetrics().density;
        this.mScaleDetector = new ScaleGestureDetector(context2, new ScaleListener());
        this.mRotateDetector = new RotateGestureDetector(context2, new RotateListener());
        this.matrix = new Matrix();
        this.f19m = new float[9];
        setImageMatrix(this.matrix);
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int pointerCount = motionEvent.getPointerCount();
                TouchImageView_shape.this.mScaleDetector.onTouchEvent(motionEvent);
                TouchImageView_shape.this.mRotateDetector.onTouchEvent(motionEvent);
                PointF pointF = new PointF(motionEvent.getX(), motionEvent.getY());
                if (pointerCount == 1) {
                    if (TouchImageView_shape.this.start != null) {
                        TouchImageView_shape.this.last.x += pointF.x - TouchImageView_shape.this.start.x;
                        TouchImageView_shape.this.last.y += pointF.y - TouchImageView_shape.this.start.y;
                    }
                    TouchImageView_shape.this.start = new PointF(pointF.x, pointF.y);
                } else {
                    TouchImageView_shape.this.start = null;
                }
                if (motionEvent.getAction() == 1) {
                    TouchImageView_shape.this.start = null;
                }
                TouchImageView_shape.this.invalidate();
                return true;
            }
        });
    }


    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (ShapeBlurActivity.border.isChecked()) {
            try {
                this.preview = svgToBitmap(this.context.getResources(), this.svgId, (int) (this.factor * ((float) this.wMask)));
                if (this.preview != null) {
                    this.mat = new Matrix();
                    this.mat.postTranslate(((this.last.x - this.f19m[2]) / this.f19m[0]) - ((this.factor * ((float) this.wMask)) / 2.0f), ((this.last.y - this.f19m[5]) / this.f19m[4]) - ((this.factor * ((float) this.wMask)) / 2.0f));
                    this.finalCanvas.drawBitmap(this.preview, this.mat, null);
                } else {
                    return;
                }
            } catch (OutOfMemoryError unused) {
                return;
            }
        }
        if (ShapeBlurActivity.bitmapClear != null) {
            try {
                if (this.bool) {
                    this.finalBitmap = ShapeBlurActivity.bitmapBlur.copy(Config.ARGB_8888, true);
                } else {
                    this.finalBitmap = ShapeBlurActivity.bitmapClear.copy(Config.ARGB_8888, true);
                }
                this.finalCanvas.setBitmap(this.finalBitmap);
                this.maskContainer = Bitmap.createBitmap(ShapeBlurActivity.f17w, ShapeBlurActivity.f16h, Config.ALPHA_8);
                this.temp.setBitmap(this.maskContainer);
            } catch (Exception unused2) {
            }
            this.mat = new Matrix();
            this.mat.setScale(this.factor, this.factor);
            this.mat.postRotate(this.mRotationDegree, (this.factor * ((float) this.wMask)) / 2.0f, (this.factor * ((float) this.wMask)) / 2.0f);
            this.mat.postTranslate(((this.last.x - this.f19m[2]) / this.f19m[0]) - ((this.factor * ((float) this.wMask)) / 2.0f), ((this.last.y - this.f19m[5]) / this.f19m[4]) - ((this.factor * ((float) this.wMask)) / 2.0f));
            this.temp.drawBitmap(this.mask, this.mat, null);
            this.finalCanvas.drawBitmap(this.maskContainer, 0.0f, 0.0f, this.paint);
            if (this.finalBitmap != null) {
                setImageBitmap(this.finalBitmap);
            }
        }
    }


    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
    }

    public void fixTrans() {
        this.matrix.getValues(this.f19m);
        float f = this.f19m[2];
        float f2 = this.f19m[5];
        float fixTrans = getFixTrans(f, (float) this.viewWidth, this.origWidth * this.saveScale);
        float fixTrans2 = getFixTrans(f2, (float) this.viewHeight, this.origHeight * this.saveScale);
        if (fixTrans != 0.0f || fixTrans2 != 0.0f) {
            this.matrix.postTranslate(fixTrans, fixTrans2);
        }
    }


    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.viewWidth = MeasureSpec.getSize(i);
        this.viewHeight = MeasureSpec.getSize(i2);
        if (!((this.oldMeasuredHeight == this.viewWidth && this.oldMeasuredHeight == this.viewHeight) || this.viewWidth == 0 || this.viewHeight == 0)) {
            this.oldMeasuredHeight = this.viewHeight;
            this.oldMeasuredWidth = this.viewWidth;
            if (this.saveScale == 1.0f) {
                Drawable drawable = getDrawable();
                if (drawable != null && drawable.getIntrinsicWidth() != 0 && drawable.getIntrinsicHeight() != 0) {
                    int intrinsicWidth = drawable.getIntrinsicWidth();
                    float f = (float) intrinsicWidth;
                    float intrinsicHeight = (float) drawable.getIntrinsicHeight();
                    float min = Math.min(((float) this.viewWidth) / f, ((float) this.viewHeight) / intrinsicHeight);
                    this.matrix.setScale(min, min);
                    float f2 = (((float) this.viewHeight) - (intrinsicHeight * min)) / 2.0f;
                    float f3 = (((float) this.viewWidth) - (f * min)) / 2.0f;
                    this.matrix.postTranslate(f3, f2);
                    this.origWidth = ((float) this.viewWidth) - (f3 * 2.0f);
                    this.origHeight = ((float) this.viewHeight) - (2.0f * f2);
                    this.initialShapePos = new PointF((float) (this.viewWidth / 2), (float) (this.viewHeight / 2));
                    int i3 = (int) f3;
                    int i4 = (int) f2;
                    this.rect = new Rect(i3, i4, ((int) this.origWidth) + i3, ((int) this.origHeight) + i4);
                    setImageMatrix(this.matrix);
                } else {
                    return;
                }
            }
            fixTrans();
            this.matrix.getValues(this.f19m);
        }
    }

    public void resetLast() {
        this.last.set(this.initialShapePos);
    }

    public Bitmap svgToBitmap(Resources resources, int i, int i2) {
        try {
            int i3 = (int) (((float) i2) * this.density);
            SVG fromResource = SVG.getFromResource(this.context, i);
            Bitmap createBitmap = Bitmap.createBitmap(i3, i3, Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.save();
            float f = (float) i3;
            float f2 = f / 2.0f;
            canvas.rotate(this.mRotationDegree, f2 / this.density, f2 / this.density);
            canvas.scale(f / (this.density * 370.0f), f / (this.density * 370.0f));
            fromResource.renderToCanvas(canvas);
            canvas.restore();
            return createBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        } catch (Exception unused) {
            return null;
        }
    }
}
