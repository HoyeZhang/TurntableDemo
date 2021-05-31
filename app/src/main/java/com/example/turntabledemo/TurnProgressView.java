package com.example.turntabledemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

/**
 * @Package: com.example.turntabledemo
 * @ClassName: TurnProgressView
 * @Description:
 * @Author: zhy
 * @CreateDate: 2021/5/27 18:11
 */
public class TurnProgressView extends View {

    private Paint paintDefault;
    private Paint paintCurrent;
    private Paint textPaint;
    private int[] colors = {Color.parseColor("#FFFC27"), Color.parseColor("#FF9686")};
    private float mTextSize;//字体大小
    private int mTextColor = Color.BLACK;

    float mPoint = 0.f;
    private float borderWidth = 18;
    private ObjectAnimator objectAnimator;

    public TurnProgressView(Context context) {
        this(context,null);
        initView();
    }

    public TurnProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
        initView();
    }

    public TurnProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TurnProgressView);
        mTextColor = ta.getColor(R.styleable.TurnProgressView_progress_color, Color.BLACK);
        mTextSize = ta.getDimension(R.styleable.TurnProgressView_progress_size, 30f);
        ta.recycle();
        initView();
    }

    private void initView() {
        paintDefault = new Paint();
        paintDefault.setColor(Color.parseColor("#66FFFFFF"));
        paintDefault.setStrokeCap(Paint.Cap.ROUND);
        paintDefault.setStyle(Paint.Style.STROKE);//设置填充样式
        paintDefault.setAntiAlias(true);//抗锯齿功能
        //圆弧宽度
        paintDefault.setStrokeWidth(borderWidth);//设置画笔宽度

        paintCurrent = new Paint();
        paintCurrent.setStyle(Paint.Style.STROKE);//设置填充样式
        paintCurrent.setAntiAlias(true);//抗锯齿功能
        paintCurrent.setStrokeWidth(borderWidth);//设置画笔宽度
        //设置笔刷的样式 Paint.Cap.Round ,Cap.SQUARE等分别为圆形、方形
        paintCurrent.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);//设置填充样式
        textPaint.setAntiAlias(true);//抗锯齿功能
        textPaint.setTextSize(mTextSize);
        textPaint.setColor(mTextColor);
        textPaint.setStrokeWidth(borderWidth);//设置画笔宽度
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        float centerX = getWidth() / 2;
        RectF oval1 = new RectF(0 + borderWidth, 0 + borderWidth, centerX * 2 - borderWidth, centerX * 2 - borderWidth);

        float[] positions = {
                0.347f,
                0.347f + (0.306f * mPoint),
        };

        //翻转画布
        canvas.translate(getWidth(), 0);
        canvas.scale(-1, 1);

        //绘制默认灰色的圆弧
        canvas.drawArc(oval1, 125, 110, false, paintDefault);//小弧形

        //渐变点位不对所以顺时针画
        SweepGradient sweepGradient = new SweepGradient(centerX, centerX, colors, positions);
//        Matrix matrix = new Matrix();
//        matrix.setRotate(180, centerX, centerX);
//        sweepGradient.setLocalMatrix(matrix);
        paintCurrent.setShader(sweepGradient);
//        canvas.drawArc(oval1, 55,-110 * mPoint, false, paintCurrent);
        canvas.drawArc(oval1, 125, 110 * mPoint, false, paintCurrent);
        // 画布恢复正常
        canvas.translate(getWidth(), 0);
        canvas.scale(-1, 1);

        float radian = (float) Math.toRadians(305);
        float x = (float) (centerX + Math.cos(radian)*centerX);
        float y = (float) (centerX + Math.sin(radian)*centerX);

        canvas.drawText((int)(mPoint * 100)+ "", x - 40f , y- 20f, textPaint);

    }


    public void setPoint(float point) {
        this.mPoint = point;
        invalidate();
    }

    public float getPoint() {
        return mPoint;
    }

    /**
     * 开始动画
     */
    public final void startAnimal(float point) {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        float oldPoint = this.mPoint;
        this.mPoint = point;

        objectAnimator = ObjectAnimator.ofFloat(this, "point", oldPoint, point);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setDuration(1000L);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
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

}
