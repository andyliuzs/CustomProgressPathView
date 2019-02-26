package com.share.jack.customviewpath.widget;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.share.jack.customviewpath.R;

/**
 * 参考 https://github.com/24Kshign/CustomViewPath
 * https://www.jianshu.com/p/f09af35a102a
 * <p>
 * <p>
 * RectF
 * https://www.jianshu.com/p/40a6c58036e4
 * https://blog.csdn.net/u013290250/article/details/54926669
 */

public class ProgressStatusView extends View {
    private static final String TAG = ProgressStatusView.class.getSimpleName();
    private int progressColor;    //进度颜色
    private int loadSuccessColor;    //成功的颜色
    private int loadFailureColor;   //失败的颜色
    private int waitingColor;
    private float progressWidth;    //进度宽度
    private float progressRadius;   //圆环半径

    private Paint mPaint;
    private ProgressStatusEnum mStatus;     //状态

    private int startAngle = -90;
    private int minAngle = -90;
    private int sweepAngle = 120;
    private int curAngle = 0;

    //追踪Path的坐标
    private PathMeasure mPathMeasure;
    //画圆的Path
    private Path mPathCircle;
    //截取PathMeasure中的path
    private Path mPathCircleDst;
    private Path successPath;
    private Path failurePathLeft;
    private Path failurePathRight;
    private Path waitingPath;

    private ValueAnimator circleAnimator;
    private float circleValue;
    private float successValue;
    private float failValueRight;
    private float failValueLeft;

    public ProgressStatusView(Context context) {
        this(context, null);
    }

    public ProgressStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressStatusView, defStyleAttr, 0);
        progressColor = array.getColor(R.styleable.ProgressStatusView_progress_color, ContextCompat.getColor(context, R.color.colorPrimary));
        loadSuccessColor = array.getColor(R.styleable.ProgressStatusView_load_success_color, ContextCompat.getColor(context, R.color.load_success));
        loadFailureColor = array.getColor(R.styleable.ProgressStatusView_load_failure_color, ContextCompat.getColor(context, R.color.load_failure));
        waitingColor = array.getColor(R.styleable.ProgressStatusView_waiting_color, ContextCompat.getColor(context, R.color.load_wait_color));
        progressWidth = array.getDimension(R.styleable.ProgressStatusView_progress_width, 6);
        progressRadius = array.getDimension(R.styleable.ProgressStatusView_progress_radius, 100);
        array.recycle();

        initPaint();
        initPath();
        initAnim();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(progressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(progressWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);    //设置画笔为圆角笔触
    }

    private void initPath() {
        mPathCircle = new Path();
        mPathMeasure = new PathMeasure();
        mPathCircleDst = new Path();
        successPath = new Path();
        failurePathLeft = new Path();
        failurePathRight = new Path();
        waitingPath = new Path();
    }

    private void initAnim() {
        circleAnimator = ValueAnimator.ofFloat(0, 1);
        circleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.YELLOW);//背景色
        Log.e(TAG, "getPaddingLeft:" + getPaddingLeft() + ",getPaddingTop:" + getPaddingTop());
        canvas.translate(getPaddingLeft(), getPaddingTop());   //将当前画布的点移到getPaddingLeft,getPaddingTop,后面的操作都以该点作为参照点
        if (mStatus == ProgressStatusEnum.Loading) {    //正在加载
            mPaint.setColor(progressColor);
            if (startAngle == minAngle) {
                sweepAngle += 6;
            }
            if (sweepAngle >= 300 || startAngle > minAngle) {
                startAngle += 6;
                if (sweepAngle > 20) {
                    sweepAngle -= 6;
                }
            }
            if (startAngle > minAngle + 300) {
                startAngle %= 360;
                minAngle = startAngle;
                sweepAngle = 20;
            }
            canvas.rotate(curAngle += 4, progressRadius + progressWidth / 2, progressRadius + progressWidth / 2);  //旋转的弧长为4
            canvas.drawArc(new RectF(progressWidth / 2, progressWidth / 2, progressRadius * 2 + progressWidth / 2, progressRadius * 2 + progressWidth / 2),
                    startAngle, sweepAngle, false, mPaint);
            invalidate();
        } else if (mStatus == ProgressStatusEnum.LoadSuccess) {     //加载成功
            mPaint.setColor(loadSuccessColor);
            mPathCircle.addCircle(getWidth() / 2  , getWidth() / 2, progressRadius - progressWidth/4, Path.Direction.CW);
            mPathMeasure.setPath(mPathCircle, false);
            mPathMeasure.getSegment(0, circleValue * mPathMeasure.getLength(), mPathCircleDst, true);   //截取path并保存到mPathCircleDst中
            canvas.drawPath(mPathCircleDst, mPaint);

            if (circleValue == 1) {      //表示圆画完了,可以钩了
                successPath.moveTo(getWidth() / 8 * 3, getWidth() / 2);
                successPath.lineTo(getWidth() / 2, getWidth() / 5 * 3);
                successPath.lineTo(getWidth() / 3 * 2, getWidth() / 5 * 2);
                mPathMeasure.nextContour();
                mPathMeasure.setPath(successPath, false);
                mPathMeasure.getSegment(0, successValue * mPathMeasure.getLength(), mPathCircleDst, true);
                canvas.drawPath(mPathCircleDst, mPaint);
            }
        } else if (mStatus == ProgressStatusEnum.LoadWaiting) {
            mPaint.setColor(waitingColor);
//            mPathCircle.addCircle(getWidth() / 2, getWidth() / 2, progressRadius, Path.Direction.CW);
//            canvas.drawPath(mPathCircle, mPaint);
//            waitingPath.moveTo(getWidth() / 2, getWidth() / 6);
//            waitingPath.lineTo(getWidth() / 2, getWidth() / 2);
//            waitingPath.lineTo(getWidth() / 2 + (getWidth() / 2 - getWidth() / 6), getWidth() / 2);
//            canvas.drawPath(waitingPath, mPaint);

            mPathCircle.addCircle(getWidth() / 2, getWidth() / 2, progressRadius- progressWidth/4, Path.Direction.CW);
            canvas.drawPath(mPathCircle, mPaint);
            waitingPath.moveTo(getWidth() / 2, getWidth() / 4);
            waitingPath.lineTo(getWidth() / 2, getWidth() / 2);
            waitingPath.lineTo(getWidth() / 2 + (getWidth() / 2 - getWidth() / 4), getWidth() / 2);
            canvas.drawPath(waitingPath, mPaint);

        } else if (mStatus == ProgressStatusEnum.LoadFailure) {      //加载失败
            mPaint.setColor(loadFailureColor);
            mPathCircle.addCircle(getWidth() / 2, getWidth() / 2, progressRadius- progressWidth/4, Path.Direction.CW);
            mPathMeasure.setPath(mPathCircle, false);
            mPathMeasure.getSegment(0, circleValue * mPathMeasure.getLength(), mPathCircleDst, true);
            canvas.drawPath(mPathCircleDst, mPaint);
            if (circleValue == 1) {  //表示圆画完了,可以画叉叉的右边部分
                failurePathRight.moveTo(getWidth() / 3 * 2, getWidth() / 3);
                failurePathRight.lineTo(getWidth() / 3, getWidth() / 3 * 2);
                mPathMeasure.nextContour();
                mPathMeasure.setPath(failurePathRight, false);
                mPathMeasure.getSegment(0, failValueRight * mPathMeasure.getLength(), mPathCircleDst, true);
                canvas.drawPath(mPathCircleDst, mPaint);
            }
            if (failValueRight == 1) {    //表示叉叉的右边部分画完了,可以画叉叉的左边部分
                failurePathLeft.moveTo(getWidth() / 3, getWidth() / 3);
                failurePathLeft.lineTo(getWidth() / 3 * 2, getWidth() / 3 * 2);
                mPathMeasure.nextContour();
                mPathMeasure.setPath(failurePathLeft, false);
                mPathMeasure.getSegment(0, failValueLeft * mPathMeasure.getLength(), mPathCircleDst, true);
                canvas.drawPath(mPathCircleDst, mPaint);
            }
        }
    }

    //重制路径
    private void resetPath() {
        successValue = 0;
        circleValue = 0;
        failValueLeft = 0;
        failValueRight = 0;
        mPathCircle.reset();
        mPathCircleDst.reset();
        failurePathLeft.reset();
        failurePathRight.reset();
        successPath.reset();
        waitingPath.reset();
    }


    private void setStatus(ProgressStatusEnum status) {
        mStatus = status;
    }

    public void loadLoading() {
        resetPath();
        setStatus(ProgressStatusEnum.Loading);
        invalidate();
    }

    public void loadSuccess() {
        resetPath();
        setStatus(ProgressStatusEnum.LoadSuccess);
        startSuccessAnim();
    }

    public void loadWaiting() {
        resetPath();
        setStatus(ProgressStatusEnum.LoadWaiting);
        invalidate();
    }

    public void loadFailure() {
        resetPath();
        setStatus(ProgressStatusEnum.LoadFailure);
        startFailAnim();
    }

    private void startSuccessAnim() {
        ValueAnimator success = ValueAnimator.ofFloat(0f, 1.0f);
        success.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                successValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        //组合动画,一先一后执行
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(success).after(circleAnimator);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    private void startFailAnim() {
        ValueAnimator failLeft = ValueAnimator.ofFloat(0f, 1.0f);
        failLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                failValueRight = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator failRight = ValueAnimator.ofFloat(0f, 1.0f);
        failRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                failValueLeft = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        //组合动画,一先一后执行
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(failLeft).after(circleAnimator).before(failRight);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        switch (mode) {
            case MeasureSpec.AT_MOST:
                Log.e(TAG, "mode ===AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                Log.e(TAG, "mode ===EXACTLY");
                break;

            case MeasureSpec.UNSPECIFIED:
                Log.e(TAG, "mode ===UNSPECIFIED");
                break;

        }
        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = (int) (2 * progressRadius + progressWidth + getPaddingLeft() + getPaddingRight());
        }

        mode = MeasureSpec.getMode(heightMeasureSpec);
        size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = (int) (2 * progressRadius + progressWidth + getPaddingTop() + getPaddingBottom());
        }
        setMeasuredDimension(width, height);
    }
}