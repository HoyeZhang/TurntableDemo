package com.example.turntabledemo.turntable;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.turntabledemo.R;

import java.util.ArrayList;
import java.util.List;

public class TurntableView extends View {

    private static final String TAG = "TurntableView";
    private Context mContext;
    private boolean isPend;//判断如果所有的item的bitmap是否加载完成
    private int mSum; //传入的item的总数
    private int mPreStartIndex;//已经准备好的bitmap


    private int mRepeatCount = 2;//转几圈
    private float mStartAngle;//存储圆盘开始的位置
    private float mItemAngle;//每个item的角度


    private int mWith;//控件的宽度
    private int mHeight;//控件的高度


    private int mRadio = 0;//圆盘的半径
    private RectF mBackRoundRect = new RectF();//圆盘的矩形
    private Paint mBackRoundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//背景圆盘


    private float mTextX;//字体的位置
    private float mTextY;//字体的位置
    private float mTextSize;//字体大小
    private int mTextColor = Color.BLACK;

    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//转盘文字画笔

    private final RectF mDrawableRect = new RectF();//头像的圆的矩阵
    private final RectF mBorderRect = new RectF();//头像的背景圆的矩阵
    private BitmapShader mBitmapShader;//头像的画笔笔头
    private final Matrix mShaderMatrix = new Matrix();//头像的矩阵
    private final Paint mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//头像的画笔
    private final Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//头像背景圆的画笔
    private int mAvatarRadio = 0;//头像的半径
    private int mBorderColor = Color.BLACK;//头像背景的圈的颜色
    private int mMySelfBorderColor = Color.WHITE;//头像背景的圈自己的颜色
    private int mBorderWidth = 0;//头像背景超过头像的值
    private List<RadialGradient> radialGradients = new ArrayList<>();
    public TurntableGiftBean mLuckyPerson;
    private ObjectAnimator objectAnimator;
    private ArrayList<TurntableGiftBean> turntableGiftBeans = new ArrayList<>();
    private int[] colors = {
            Color.WHITE,
            Color.BLUE,
            Color.YELLOW,
            Color.YELLOW,
            Color.WHITE,
            Color.BLUE,
            Color.BLUE};

    OnAnimalEndListener mOnAnimalEndListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    isPend = true;
                    invalidate();
                    break;
                case 1:

                    startAnimal();

                    break;
            }


        }
    };

    public TurntableView(Context context) {
        this(context, (AttributeSet) null);
    }

    public TurntableView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TurntableView(Context context, @Nullable AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.TurntableView);
        mBorderColor = ta.getColor(R.styleable.TurntableView_avatar_background, Color.BLACK);
        mTextColor = ta.getColor(R.styleable.TurntableView_gift_name_color, Color.BLACK);
        mTextSize = ta.getDimension(R.styleable.TurntableView_gift_name_size, 20f);
        mBorderWidth = (int) ta.getDimension(R.styleable.TurntableView_avatar_border_size, 0f);
        ta.recycle();
        init();
    }

    private final void init() {
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStrokeWidth(3.0F);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
    }

    public void addListener(OnAnimalEndListener onAnimalEndListener) {
        mOnAnimalEndListener = onAnimalEndListener;
    }

    public final void setData(ArrayList<TurntableGiftBean> participantUsersBeanList) {
        mSum = participantUsersBeanList.size();
        mItemAngle = 360.0F / mSum;//计算每个item的角度
        mStartAngle = -mItemAngle / 2;
        turntableGiftBeans.clear();
        for (int i = 0; i < mSum; i++) {
            TurntableGiftBean luckyPersonBean = new TurntableGiftBean();
            luckyPersonBean.setmCenterAngle(mStartAngle + mItemAngle * (float) i + mItemAngle / (float) 2);
            luckyPersonBean.setId(participantUsersBeanList.get(i).getId());
            luckyPersonBean.setGiftName(participantUsersBeanList.get(i).getGiftName());
            luckyPersonBean.setGiftImageUrl(participantUsersBeanList.get(i).getGiftImageUrl());
            luckyPersonBean.setGiftImage(mContext, participantUsersBeanList.get(i).getGiftImageUrl(), R.mipmap.ic_roulette_on_person_avatar, new BitmapNoStatusCallBack() {
                @Override
                public void onResult() {
                    mPreStartIndex++;
                    if (mPreStartIndex == mSum) {
                        mHandler.sendEmptyMessage(0);
                    }
                }
            });

            turntableGiftBeans.add(luckyPersonBean);
        }

    }

    public void setLuckyPosition(int giftId) {
        for (int i = 0; i < turntableGiftBeans.size(); i++) {
            if (giftId == turntableGiftBeans.get(i).getId()) {
                mLuckyPerson = turntableGiftBeans.get(i);
                mHandler.sendEmptyMessage(1);
                break;
            }
            ;
        }
    }

    /**
     * 清除上次滚动带来的数据叠加
     */
    public final void clear() {
        isPend = false;//判断如果所有的item的bitmap是否加载完成
        mSum = 0;//传入的item的总数
        mPreStartIndex = 0;//已经准备好的bitmap
        mStartAngle = 0.0F;//存储圆盘开始的位置
        mItemAngle = 0.0F;//每个item的角度
        turntableGiftBeans.clear();
        mLuckyPerson = null;
    }

    /**
     * 计算字体存在的位置
     */
    private final void calculateTextLocal() {
        //名字的矩阵位置距离圆心4/10的地方  用于不画弧度的
        mTextX = (float) mRadio;
        mTextY = (float) (mRadio * 2) / 10.0F;//6/10是屏幕距离文字的地方
    }

    /**
     * 计算一下头像所在的位置
     */
    private final void calculateAvatarRetF() {
        mBorderRect.set((float) mRadio - (float) mAvatarRadio, (float) mRadio / (float) 4 - (float) mAvatarRadio, (float) mRadio + (float) mAvatarRadio, (float) mRadio / (float) 4 + (float) mAvatarRadio);
        mDrawableRect.set((float) mRadio - (float) mAvatarRadio, (float) mRadio / (float) 4 - (float) mAvatarRadio, (float) mRadio + (float) mAvatarRadio, (float) mRadio / (float) 4 + (float) mAvatarRadio);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        mWith = MeasureSpec.getSize(widthMeasureSpec);
        //mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mRadio =  mWith / 2;//设置半径
        createRadialGradientList();
        //确定一下圆心的坐标
        mBackRoundRect.top = 0.0F;
        mBackRoundRect.left = 0.0F;
        mBackRoundRect.right = (float) mRadio * 2.0F;
        mBackRoundRect.bottom = (float) mRadio * 2.0F;
        mAvatarRadio = mRadio / 6;
        //计算一下字体的位置
        calculateTextLocal();
        //计算头像所在的位置
        calculateAvatarRetF();
    }

    private void createRadialGradientList() {
        if (radialGradients.size() == 0) {
            for (int i = 0; i < colors.length; i++) {
                RadialGradient radialGradient = new RadialGradient(mRadio, mRadio, mRadio, new int[]{colors[i], Color.RED}, null, Shader.TileMode.CLAMP);
                radialGradients.add(radialGradient);
            }
        }

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isPend) {
            //画表盘

            // 先旋转 后作画图 （作画后再旋转就没用了）
            canvas.rotate(-90.0F, (float) mRadio, (float) mRadio);
            for (int index = 0; index < mSum; index++) {
                //画各个item的扇形
                drawCircular(canvas, index);
                //画名字
                drawName(canvas, index);
                //画头像
                drawCircleAvatar(canvas, index);
            }
        }
    }

    /**
     * 画扇形
     */
    private final void drawCircular(Canvas canvas, int index) {
        mBackRoundPaint.setColor(colors[index]);

        if (radialGradients.size() > index) {
            mBackRoundPaint.setShader(radialGradients.get(index));
        } else {
            mBackRoundPaint.setShader(radialGradients.get(0));
        }

        canvas.drawArc(mBackRoundRect, mStartAngle + mItemAngle * (float) index, mItemAngle, true, mBackRoundPaint);
    }

    /**
     * 画名字
     */
    public final void drawName(Canvas canvas, int index) {
        //save()跟restore()要成对存在 -- 表示在save到restore中的旋转放大缩小等 只对这两方法间的绘画起作用
        canvas.save();
        canvas.rotate(90.0F, (float) mRadio, (float) mRadio);//将原来的画布-90旋转回来90回来
        canvas.rotate(turntableGiftBeans.get(index).getmCenterAngle(), (float) mRadio, (float) mRadio);
        // 再进行item中心角度的旋转，并画头像
        canvas.drawText(turntableGiftBeans.get(index).getGiftName(), mTextX, mTextY, mTextPaint);
        canvas.restore();
    }

    /**
     * 画头像
     */
    private void drawCircleAvatar(Canvas canvas, int i) {
        mBitmapShader = new BitmapShader(turntableGiftBeans.get(i).getGiftImageBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);
        mBorderPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);

        updateShaderMatrix(turntableGiftBeans.get(i).getGiftImageBitmap());//对图片进行缩小跟移动中心点
        canvas.save();
        canvas.rotate(90.0F, (float) mRadio, (float) mRadio);
        canvas.rotate(turntableGiftBeans.get(i).getmCenterAngle(), (float) mRadio, (float) mRadio);
//        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), (float) mAvatarRadio + (float) mBorderWidth, mBorderPaint);
//        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), (float) mAvatarRadio, mBitmapPaint);
        canvas.drawBitmap(turntableGiftBeans.get(i).getGiftImageBitmap(), mShaderMatrix, mBitmapPaint);
        canvas.restore();
    }

    /**
     * 对图片进行缩小跟移动中心点
     */
    private void updateShaderMatrix(Bitmap oldbitmap) {
        mShaderMatrix.set(null);
        float scale = mDrawableRect.width() / (float) oldbitmap.getWidth();
        float dx = (mDrawableRect.width() - (float) oldbitmap.getWidth() * scale) * 0.5F;
        float dy = (mDrawableRect.height() - (float) oldbitmap.getHeight() * scale) * 0.5F;
        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((float) ((int) (dx + 0.5F)) +  /**起始位置也要加上**/mDrawableRect.left, (float) ((int) (dy + 0.5F)) +  /**起始位置也要加上**/oldbitmap.getHeight() * scale - 30);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    /**
     * 开始动画
     */
    public final void startAnimal() {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }

        float endAngle = mRepeatCount * 360 + (360 - mLuckyPerson.getmCenterAngle());
        objectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0, endAngle / 2, endAngle - 4, endAngle + 2, endAngle);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setDuration(4000L);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                mOnAnimalEndListener.onEnd();
//                Log.i(TAG, "onAnimationEnd:角度：" + mLuckyPerson.mCenterAngle + "名字" + mLuckyPerson.nick);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        objectAnimator.start();
    }

    /**
     * 结束动画
     */
    public final void stopAnimal() {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    public interface OnAnimalEndListener {
        void onEnd();
    }


}