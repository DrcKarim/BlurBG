package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.helper;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class ImageViewTouch extends ImageViewTouchBase {

    protected static final float MIN_DIST = 10F;

    static final float MIN_ZOOM = 1.0f;
    protected GestureDetector mGestureDetector;
    protected int mTouchSlop;
    protected float mCurrentScaleFactor;
    protected float mScaleFactor;
    protected int mDoubleTapDirection;
    protected GestureListener mGestureListener;
    protected OnZoomPlusClickListener mZoomPlusClickListener;
    protected OnZoomMinusClickListener mZoomMinusClickListener;
    protected Integer xPOS;
    protected Integer yPOS;

    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist;

    private final static int NONE = 0;
    private final static int DRAG = 1;
    private final static int ZOOM = 2;

    private int touchState = NONE;

    public ImageViewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
        mTouchSlop = ViewConfiguration.getTouchSlop();
        mGestureListener = new GestureListener();
        mZoomPlusClickListener = new OnZoomPlusClickListener();
        mZoomMinusClickListener = new OnZoomMinusClickListener();

        mGestureDetector = new GestureDetector(getContext(), mGestureListener);
        mCurrentScaleFactor = 1f;
        mDoubleTapDirection = 1;
    }

    public View.OnClickListener getZoomPlusClickLitener() {
        return mZoomPlusClickListener;
    }

    public View.OnClickListener getZoomMinusClickLitener() {
        return mZoomMinusClickListener;
    }

    @Override
    public void setImageRotateBitmapReset(RotateBitmap bitmap, boolean reset) {
        super.setImageRotateBitmapReset(bitmap, reset);
        mScaleFactor = getMaxZoom() / 3;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchState != ZOOM)
            mGestureDetector.onTouchEvent(event);
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                start.set(event.getX(), event.getY());
                touchState = DRAG;
                break;
            case MotionEvent.ACTION_UP:
                touchState = NONE;
                if (getScale() < 1f) {
                    zoomTo(1f, 50);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                touchState = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > MIN_DIST) {
                    touchState = ZOOM;
                    midPoint(mid, event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchState == ZOOM) {
                    onScale(event);
                }
                break;
        }
        return true;
    }

    // calculate the distance
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // calculate the center point
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public boolean onScale(MotionEvent event) {
        float span = spacing(event);
        float targetScale = span / oldDist;
        if (span > MIN_DIST) {
            targetScale = Math.min(getMaxZoom(),
                    Math.max(targetScale, MIN_ZOOM));
            zoomTo(targetScale, mid.x, mid.y);
            mCurrentScaleFactor = Math.min(getMaxZoom(),
                    Math.max(targetScale, MIN_ZOOM));
            mDoubleTapDirection = 1;
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    protected void onZoom(float scale) {
        super.onZoom(scale);
        mCurrentScaleFactor = scale;
    }

    protected float onDoubleTapPost(float scale, float maxZoom) {
        if (mDoubleTapDirection == 1) {
            if ((scale + (mScaleFactor * 2)) <= maxZoom) {
                return scale + mScaleFactor;
            } else {
                mDoubleTapDirection = -1;
                return maxZoom;
            }
        } else {
            mDoubleTapDirection = 1;
            return 1f;
        }
    }

    protected float onZoomInPost(float scale, float maxZoom) {
        if ((scale + (mScaleFactor * 2)) <= maxZoom) {
            return scale + mScaleFactor;
        } else {
            return maxZoom;
        }
    }


    class OnZoomPlusClickListener implements View.OnClickListener {

        public void onClick(View v) {
            float scale = getScale();
            float targetScale = scale;
            targetScale = onZoomInPost(scale, getMaxZoom());
            targetScale = Math.min(getMaxZoom(),
                    Math.max(targetScale, MIN_ZOOM));
            mCurrentScaleFactor = targetScale;

            zoomTo(targetScale, 0.5f * X_CENTER * mBitmapDisplayed.getWidth(), 0.5f * Y_CENTER * mBitmapDisplayed.getHeight(), 200);
            //zoomTo(targetScale, 0f, 0f, 200);
            invalidate();
        }

    }

    class OnZoomMinusClickListener implements View.OnClickListener {

        public void onClick(View v) {
            float scale = getScale();
            float targetScale = scale;
            targetScale = scale / 2f;
            targetScale = Math.min(getMaxZoom(),Math.max(targetScale, MIN_ZOOM));
            mCurrentScaleFactor = targetScale;
            zoomTo(targetScale, 50, 50, 200);
            invalidate();
        }

    }


    class GestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float scale = getScale();
            float targetScale = scale;
            targetScale = onDoubleTapPost(scale, getMaxZoom());
            targetScale = Math.min(getMaxZoom(),
                    Math.max(targetScale, MIN_ZOOM));
            mCurrentScaleFactor = targetScale;
            zoomTo(targetScale, e.getX(), e.getY(), 200);
            invalidate();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (e1 == null || e2 == null)
                return false;
            if (touchState != DRAG)
                return false;
            if (getScale() == 1f)
                return false;
            scrollBy(-distanceX, -distanceY);
            invalidate();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (touchState != DRAG)
                return false;

            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(velocityX) > 800 || Math.abs(velocityY) > 800) {
                scrollBy(diffX / 2, diffY / 2, 300);
                invalidate();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }


    public void setViewSetCenter(float xPOS, float yPOS) {

    }
}