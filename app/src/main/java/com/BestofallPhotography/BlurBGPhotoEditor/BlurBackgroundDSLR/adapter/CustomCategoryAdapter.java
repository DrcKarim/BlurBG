package com.BestofallPhotography.BlurBGPhotoEditor.BlurBackgroundDSLR.adapter;

import android.content.Context;
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


public class CustomCategoryAdapter extends Adapter<CustomCategoryAdapter.ItemHolder> {
    RecyclerView catRecView;
    Context context;


    public class ItemHolder extends ViewHolder {
        ImageView categoryItem;

        public ItemHolder(View view) {
            super(view);
            this.categoryItem = (ImageView) view.findViewById(R.id.categoryItem);
        }
    }

    public CustomCategoryAdapter(Context context2, RecyclerView recyclerView) {
        this.context = context2;
        this.catRecView = recyclerView;
    }

    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemHolder(((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.shape_blur_category_item, viewGroup, false));
    }

    public void onBindViewHolder(ItemHolder itemHolder, final int i) {
        Glide.with(this.context).load(Integer.valueOf(ShapeBlurActivity.categoryID[i])).apply(new RequestOptions().fitCenter()).into(itemHolder.categoryItem);
        itemHolder.categoryItem.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ShapeBlurActivity.categoryIndex = 1 - i;
                ShapeBlurActivity.imageView.lastCatIndex = ShapeBlurActivity.categoryIndex;
                ShapeBlurActivity.shapeAdapter.notifyDataSetChanged();
                ShapeBlurActivity.shapeRecyclerView.scrollToPosition(ShapeBlurActivity.shapeID[ShapeBlurActivity.categoryIndex].length - 1);
                ShapeBlurActivity.imageView.lastPosIndex = -1;
                CustomCategoryAdapter.this.notifyDataSetChanged();
            }
        });
        if (ShapeBlurActivity.imageView.lastCatIndex == 1 - i) {
            Glide.with(this.context).load(Integer.valueOf(ShapeBlurActivity.selectedCategoryID[i])).apply(new RequestOptions().fitCenter()).into(itemHolder.categoryItem);
        }
    }

    public int getItemCount() {
        return ShapeBlurActivity.categoryID.length;
    }
}
