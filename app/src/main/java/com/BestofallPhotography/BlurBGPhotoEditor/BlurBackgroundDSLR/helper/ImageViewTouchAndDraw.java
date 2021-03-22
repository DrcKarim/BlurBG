package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ImageViewTouchAndDraw extends ImageViewTouch {
    protected static final float TOUCH_TOLERANCE = 4.0f;
    protected Canvas mCanvas;
    protected Bitmap mCopy;
    private OnDrawStartListener mDrawListener;
    protected Matrix mIdentityMatrix = new Matrix();
    protected Matrix mInvertedMatrix = new Matrix();
    protected Paint mPaint;
    protected TouchMode mTouchMode = TouchMode.DRAW;
    protected float mX;
    protected float mY;
    protected Path tmpPath = new Path();

    public interface OnDrawStartListener {
        void onDrawStart();
    }

    public enum TouchMode {
        IMAGE,
        DRAW
    }

    public ImageViewTouchAndDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnDrawStartListener(OnDrawStartListener listener) {
        this.mDrawListener = listener;
    }

    protected void init() {
        super.init();
        this.mPaint = new Paint(1);
        this.mPaint.setFilterBitmap(false);
        this.mPaint.setColor(0xFFFF0000);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setStrokeCap(Cap.ROUND);
        this.mPaint.setStrokeWidth(10.0f);
        this.tmpPath = new Path();
    }

    public TouchMode getDrawMode() {
        return this.mTouchMode;
    }

    public void setDrawMode(TouchMode mode) {
        if (mode != this.mTouchMode) {
            this.mTouchMode = mode;
            onDrawModeChanged();
        }
    }

    protected void onDrawModeChanged() {
        if (this.mTouchMode == TouchMode.DRAW) {
            Matrix m1 = new Matrix(getImageMatrix());
            this.mInvertedMatrix.reset();
            float[] v1 = getMatrixValues(m1);
            m1.invert(m1);
            float[] v2 = getMatrixValues(m1);
            this.mInvertedMatrix.postTranslate(-v1[2], -v1[5]);
            this.mInvertedMatrix.postScale(v2[0], v2[4]);
            this.mCanvas.setMatrix(this.mInvertedMatrix);
        }
    }

    public Paint getPaint() {
        return this.mPaint;
    }

    public void setPaint(Paint paint) {
        this.mPaint.set(paint);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mCopy != null) {
            int saveCount = canvas.getSaveCount();
            canvas.save();
            canvas.drawBitmap(this.mCopy, getImageMatrix(), null);
            canvas.restoreToCount(saveCount);
        }
    }

    public void commit(Canvas canvas) {
        canvas.drawBitmap(getDisplayBitmap().getBitmap(), new Matrix(), null);
        canvas.drawBitmap(this.mCopy, new Matrix(), null);
    }

    protected void onBitmapChanged(RotateBitmap bitmap) {
        super.onBitmapChanged(bitmap);
        if (this.mCopy != null) {
            this.mCopy.recycle();
            this.mCopy = null;
        }
        if (bitmap != null && bitmap.getBitmap() != null) {
            this.mCopy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            this.mCanvas = new Canvas(this.mCopy);
            this.mCanvas.drawColor(0);
            onDrawModeChanged();
        }
    }

    private void touch_start(float x, float y) {
        this.tmpPath.reset();
        this.tmpPath.moveTo(x, y);
        this.mX = x;
        this.mY = y;
        if (this.mDrawListener != null) {
            this.mDrawListener.onDrawStart();
        }
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - this.mX);
        float dy = Math.abs(y - this.mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            this.tmpPath.quadTo(this.mX, this.mY, (this.mX + x) / 2.0f, (this.mY + y) / 2.0f);
            this.mCanvas.drawPath(this.tmpPath, this.mPaint);
            this.tmpPath.reset();
            this.tmpPath.moveTo((this.mX + x) / 2.0f, (this.mY + y) / 2.0f);
            this.mX = x;
            this.mY = y;
        }
    }

    public static float[] getMatrixValues(Matrix m) {
        float[] values = new float[9];
        m.getValues(values);
        return values;
    }

    private void touch_up() {
        this.tmpPath.reset();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mTouchMode == TouchMode.DRAW && event.getPointerCount() == 1) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    return true;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    return true;
                default:
                    return true;
            }
        } else if (mTouchMode == TouchMode.IMAGE) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }

    public Bitmap getOverlayBitmap() {
        return this.mCopy;
    }
}
