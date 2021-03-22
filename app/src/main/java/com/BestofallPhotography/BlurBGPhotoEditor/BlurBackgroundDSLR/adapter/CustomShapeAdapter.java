package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.R;
import com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.activity.ShapeBlurActivity;


public class CustomShapeAdapter extends Adapter<CustomShapeAdapter.ItemHolder> {
    Context context;
    RecyclerView mRecyclerView;


    public class ItemHolder extends ViewHolder {
        ImageView shapeItem;

        public ItemHolder(View view) {
            super(view);
            this.shapeItem = (ImageView) view.findViewById(R.id.shapeItem);
        }
    }

    public CustomShapeAdapter(Context context2, RecyclerView recyclerView) {
        this.context = context2;
        this.mRecyclerView = recyclerView;
    }

    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemHolder(((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.shape_blur_shape_item, viewGroup, false));
    }

    public void onBindViewHolder(ItemHolder itemHolder, final int i) {
        Glide.with(this.context).load(Integer.valueOf(ShapeBlurActivity.shapeButtonID[ShapeBlurActivity.categoryIndex][i])).apply(new RequestOptions().fitCenter()).into(itemHolder.shapeItem);
        itemHolder.shapeItem.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ShapeBlurActivity.imageView.setImageResource(ShapeBlurActivity.shapeID[ShapeBlurActivity.categoryIndex][i]);
                if (!ShapeBlurActivity.imageView.hover) {
                    ShapeBlurActivity.imageView.hover = true;
                }
                if (ShapeBlurActivity.imageView.lastCatIndex == ShapeBlurActivity.categoryIndex && ShapeBlurActivity.imageView.lastPosIndex == i) {
                    if (ShapeBlurActivity.imageView.bool) {
                        ShapeBlurActivity.imageView.paint.setShader(ShapeBlurActivity.imageView.shader2);
                        ShapeBlurActivity.imageView.setImageBitmap(ShapeBlurActivity.bitmapClear);
                    } else {
                        ShapeBlurActivity.imageView.paint.setShader(ShapeBlurActivity.imageView.shader1);
                        ShapeBlurActivity.imageView.setImageBitmap(ShapeBlurActivity.bitmapBlur);
                    }
                    ShapeBlurActivity.imageView.bool = true ^ ShapeBlurActivity.imageView.bool;
                    ShapeBlurActivity.imageView.invalidate();
                    return;
                }
                ShapeBlurActivity.imageView.paint.setShader(ShapeBlurActivity.imageView.shader1);
                ShapeBlurActivity.imageView.setImageBitmap(ShapeBlurActivity.bitmapBlur);
                ShapeBlurActivity.imageView.bool = true;
                ShapeBlurActivity.imageView.lastCatIndex = ShapeBlurActivity.categoryIndex;
                ShapeBlurActivity.imageView.lastPosIndex = i;
                CustomShapeAdapter.this.notifyDataSetChanged();
                ShapeBlurActivity.imageView.resetLast();
                ShapeBlurActivity.imageView.mask = BitmapFactory.decodeResource(CustomShapeAdapter.this.context.getResources(), ShapeBlurActivity.shapeID[ShapeBlurActivity.categoryIndex][i]);
                ShapeBlurActivity.imageView.spot = BitmapFactory.decodeResource(CustomShapeAdapter.this.context.getResources(), ShapeBlurActivity.shapeID[ShapeBlurActivity.categoryIndex][i]).copy(Config.ALPHA_8, true);
                ShapeBlurActivity.imageView.svgId = ShapeBlurActivity.shapeViewID[ShapeBlurActivity.categoryIndex][i];
                ShapeBlurActivity.imageView.wMask = ShapeBlurActivity.imageView.mask.getWidth();
                ShapeBlurActivity.imageView.mRotationDegree = 0.0f;
                ShapeBlurActivity.imageView.canvasMask = new Canvas(ShapeBlurActivity.imageView.maskContainer);
                ShapeBlurActivity.imageView.invalidate();
            }
        });
        if (ShapeBlurActivity.imageView.hover && ShapeBlurActivity.imageView.lastPosIndex == i) {
            Resources resources = this.context.getResources();
            itemHolder.shapeItem.setImageDrawable(new LayerDrawable(new Drawable[]{ResourcesCompat.getDrawable(resources, ShapeBlurActivity.shapeButtonID[ShapeBlurActivity.categoryIndex][i], null), ResourcesCompat.getDrawable(resources, R.drawable.hover1, null)}));
        }
    }

    public int getItemCount() {
        return ShapeBlurActivity.shapeID[ShapeBlurActivity.categoryIndex].length;
    }
}
