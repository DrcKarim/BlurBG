package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.gesture;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;


public abstract class TwoFingerGestureDetector extends BaseGestureDetector {
    private float mBottomSlopEdge;
    protected float mCurrFingerDiffX;
    protected float mCurrFingerDiffY;
    private float mCurrLen;
    private final float mEdgeSlop;
    protected float mPrevFingerDiffX;
    protected float mPrevFingerDiffY;
    private float mPrevLen;
    private float mRightSlopEdge;


    public abstract void handleInProgressEvent(int i, MotionEvent motionEvent);


    public abstract void handleStartProgressEvent(int i, MotionEvent motionEvent);

    public TwoFingerGestureDetector(Context context) {
        super(context);
        this.mEdgeSlop = (float) ViewConfiguration.get(context).getScaledEdgeSlop();
    }


    public void updateStateByEvent(MotionEvent motionEvent) {
        super.updateStateByEvent(motionEvent);
        MotionEvent motionEvent2 = this.mPrevEvent;
        this.mCurrLen = -1.0f;
        this.mPrevLen = -1.0f;
        float y = motionEvent2.getY(1) - motionEvent2.getY(0);
        this.mPrevFingerDiffX = motionEvent2.getX(1) - motionEvent2.getX(0);
        this.mPrevFingerDiffY = y;
        float y2 = motionEvent.getY(1) - motionEvent.getY(0);
        this.mCurrFingerDiffX = motionEvent.getX(1) - motionEvent.getX(0);
        this.mCurrFingerDiffY = y2;
    }

    public float getCurrentSpan() {
        if (this.mCurrLen == -1.0f) {
            float f = this.mCurrFingerDiffX;
            float f2 = this.mCurrFingerDiffY;
            this.mCurrLen = (float) Math.sqrt((double) ((f * f) + (f2 * f2)));
        }
        return this.mCurrLen;
    }

    public float getPreviousSpan() {
        if (this.mPrevLen == -1.0f) {
            float f = this.mPrevFingerDiffX;
            float f2 = this.mPrevFingerDiffY;
            this.mPrevLen = (float) Math.sqrt((double) ((f * f) + (f2 * f2)));
        }
        return this.mPrevLen;
    }

    protected static float getRawX(MotionEvent motionEvent, int i) {
        float x = motionEvent.getX() - motionEvent.getRawX();
        if (i < motionEvent.getPointerCount()) {
            return motionEvent.getX(i) + x;
        }
        return 0.0f;
    }

    protected static float getRawY(MotionEvent motionEvent, int i) {
        float y = motionEvent.getY() - motionEvent.getRawY();
        if (i < motionEvent.getPointerCount()) {
            return motionEvent.getY(i) + y;
        }
        return 0.0f;
    }


    public boolean isSloppyGesture(MotionEvent motionEvent) {
        DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
        this.mRightSlopEdge = ((float) displayMetrics.widthPixels) - this.mEdgeSlop;
        this.mBottomSlopEdge = ((float) displayMetrics.heightPixels) - this.mEdgeSlop;
        float f = this.mEdgeSlop;
        float f2 = this.mRightSlopEdge;
        float f3 = this.mBottomSlopEdge;
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        float rawX2 = getRawX(motionEvent, 1);
        float rawY2 = getRawY(motionEvent, 1);
        boolean z = rawX < f || rawY < f || rawX > f2 || rawY > f3;
        boolean z2 = rawX2 < f || rawY2 < f || rawX2 > f2 || rawY2 > f3;
        if ((!z || !z2) && !z && !z2) {
            return false;
        }
        return true;
    }
}
