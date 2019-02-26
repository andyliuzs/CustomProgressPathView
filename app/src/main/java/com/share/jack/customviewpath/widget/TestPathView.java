package com.share.jack.customviewpath.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TestPathView extends View {
    private static final String TAG = TestPathView.class.getSimpleName();
    private Paint mPaint = null;
    private Path mPath = null;

    public TestPathView(Context context) {
        this(context, null);
    }

    public TestPathView(Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);
    }

    public TestPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        //初始化Paint
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10f);
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

////将坐标系原点从（0,0）移动到（100,100）
//        mPath.moveTo(100, 100);
////画从（100,100）到（400,400）之间的直线
//        mPath.lineTo(400, 400);
////path.rMoveTo(0, 100); //暂时注释
//        mPath.lineTo(400, 800);
//        canvas.drawPath(mPath, mPaint);
        //将坐标系原点从（0,0）移动到（100,100）

        //setLastPoint使用
//        mPath.moveTo(100, 100);
////画从（100,100）到（400,400）之间的直线
//        mPath.lineTo(400, 400);
////新加的setLastPoint
//        mPath.setLastPoint(100, 800);
//        mPath.lineTo(400, 800);
//        canvas.drawPath(mPath, mPaint);


        //offset使用
//        mPath.moveTo(0, 0);
//        mPath.lineTo(200, 200);
//        canvas.drawPath(mPath, mPaint);
//
//        mPath.offset(500, 0);
//        canvas.drawPath(mPath, mPaint);



    }


}
