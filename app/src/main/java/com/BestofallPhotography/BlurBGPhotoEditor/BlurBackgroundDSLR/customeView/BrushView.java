package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.customeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;


public class BrushView extends View {
    public BrushSize brushSize;
    public boolean isBrushSize = true;
    public float opacity;
    public float ratioRadius;

    public BrushView(Context context) {
        super(context);
        initMyView();
    }

    public BrushView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initMyView();
    }

    public BrushView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initMyView();
    }

    public void initMyView() {
        this.brushSize = new BrushSize();
    }


    public void onDraw(Canvas canvas) {
        float f;
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (width != 0 && height != 0) {
            float f2 = ((float) width) / 2.0f;
            float f3 = ((float) height) / 2.0f;
            if (width > height) {
                f = (this.ratioRadius * TouchImageView.resRatio) / 2.0f;
            } else {
                f = (this.ratioRadius * TouchImageView.resRatio) / 2.0f;
            }
            if (((int) f) * 2 > 150) {
                LayoutParams layoutParams = (LayoutParams) getLayoutParams();
                int i = ((int) (2.0f * f)) + 40;
                layoutParams.height = i;
                layoutParams.width = i;
                layoutParams.alignWithParent = true;
                setLayoutParams(layoutParams);
            }
            this.brushSize.setCircle(f2, f3, f, Direction.CCW);
            canvas.drawPath(this.brushSize.getPath(), this.brushSize.getPaint());
            if (!this.isBrushSize) {
                canvas.drawPath(this.brushSize.getPath(), this.brushSize.getInnerPaint());
            }
        }
    }

    public void setShapeRadiusRatio(float f) {
        this.ratioRadius = f;
    }

    public void setShapeOpacity(float f) {
        this.opacity = f;
    }
}
