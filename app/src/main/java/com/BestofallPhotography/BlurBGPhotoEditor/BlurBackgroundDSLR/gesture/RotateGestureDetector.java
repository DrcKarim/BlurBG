package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.gesture;

import android.content.Context;
import android.view.MotionEvent;


public class RotateGestureDetector extends TwoFingerGestureDetector {
    private final OnRotateGestureListener mListener;
    private boolean mSloppyGesture;


    public interface OnRotateGestureListener {
        boolean onRotate(RotateGestureDetector rotateGestureDetector);

        boolean onRotateBegin(RotateGestureDetector rotateGestureDetector);

        void onRotateEnd(RotateGestureDetector rotateGestureDetector);
    }


    public static class SimpleOnRotateGestureListener implements OnRotateGestureListener {
        public boolean onRotate(RotateGestureDetector rotateGestureDetector) {
            return false;
        }

        public boolean onRotateBegin(RotateGestureDetector rotateGestureDetector) {
            return true;
        }

        public void onRotateEnd(RotateGestureDetector rotateGestureDetector) {
        }
    }

    public RotateGestureDetector(Context context, OnRotateGestureListener onRotateGestureListener) {
        super(context);
        this.mListener = onRotateGestureListener;
    }


    public void handleStartProgressEvent(int i, MotionEvent motionEvent) {
        if (i != 2) {
            switch (i) {
                case 5:
                    resetState();
                    this.mPrevEvent = MotionEvent.obtain(motionEvent);
                    this.mTimeDelta = 0;
                    updateStateByEvent(motionEvent);
                    this.mSloppyGesture = isSloppyGesture(motionEvent);
                    if (!this.mSloppyGesture) {
                        this.mGestureInProgress = this.mListener.onRotateBegin(this);
                        return;
                    }
                    return;
                case 6:
                    if (this.mSloppyGesture) {
                        return;
                    }
                    return;
                default:
                    return;
            }
        } else if (this.mSloppyGesture) {
            this.mSloppyGesture = isSloppyGesture(motionEvent);
            if (!this.mSloppyGesture) {
                this.mGestureInProgress = this.mListener.onRotateBegin(this);
            }
        }
    }


    public void handleInProgressEvent(int i, MotionEvent motionEvent) {
        if (i != 6) {
            switch (i) {
                case 2:
                    updateStateByEvent(motionEvent);
                    if (this.mCurrPressure / this.mPrevPressure > 0.0f && this.mListener.onRotate(this)) {
                        this.mPrevEvent.recycle();
                        this.mPrevEvent = MotionEvent.obtain(motionEvent);
                        return;
                    }
                    return;
                case 3:
                    if (!this.mSloppyGesture) {
                        this.mListener.onRotateEnd(this);
                    }
                    resetState();
                    return;
                default:
                    return;
            }
        } else {
            updateStateByEvent(motionEvent);
            if (!this.mSloppyGesture) {
                this.mListener.onRotateEnd(this);
            }
            resetState();
        }
    }


    public void resetState() {
        super.resetState();
        this.mSloppyGesture = false;
    }

    public float getRotationDegreesDelta() {
        return (float) (((Math.atan2((double) this.mPrevFingerDiffY, (double) this.mPrevFingerDiffX) - Math.atan2((double) this.mCurrFingerDiffY, (double) this.mCurrFingerDiffX)) * 180.0d) / 3.141592653589793d);
    }
}
