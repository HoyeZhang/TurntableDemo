package com.example.turntabledemo.turntable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.turntabledemo.R;

/**
 * @Package: com.example.turntabledemo.turntable
 * @ClassName: GiftBean
 * @Description:
 * @Author: zhy
 * @CreateDate: 2021/5/26 18:02
 */
public class GiftBean {
    private long id;
    private String giftName;
    private String giftImageUrl;
    private Bitmap giftImageBitmap;
    private float mCenterAngle;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getGiftImageUrl() {
        return giftImageUrl;
    }

    public void setGiftImageUrl(String giftImageUrl) {
        this.giftImageUrl = giftImageUrl;
    }


    public void setGiftImage(final Context context, final String avater, int resourceId, final BitmapNoStatusCallBack bitmapCallBack) {
        Glide.with(context.getApplicationContext())
                .applyDefaultRequestOptions(new RequestOptions().centerCrop())
                .asBitmap()
                .load(avater)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        giftImageBitmap = resource;
                        bitmapCallBack.onResult();
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        giftImageBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_roulette_on_person_avatar);
                        bitmapCallBack.onResult();
                    }
                });
    }

    public Bitmap getGiftImageBitmap() {
        return giftImageBitmap;
    }

    public void setGiftImageBitmap(Bitmap giftImageBitmap) {
        this.giftImageBitmap = giftImageBitmap;
    }

    public float getmCenterAngle() {
        return mCenterAngle;
    }

    public void setmCenterAngle(float mCenterAngle) {
        this.mCenterAngle = mCenterAngle;
    }
}
