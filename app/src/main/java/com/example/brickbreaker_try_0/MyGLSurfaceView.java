package com.example.brickbreaker_try_0;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import androidx.constraintlayout.widget.Guideline;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyGLSurfaceView extends GLSurfaceView {
    MyGLRenderer myGLRenderer;
    public static float screenHeight, screenWidth;
    private float guideLinePercent = 0.1f;
    //private Guideline guideLine;
    private final int X = 0, Y = 1, Z = 2;
    public static boolean platformPositionLock = false;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);

        myGLRenderer = new MyGLRenderer(context);
        setRenderer(myGLRenderer);

        //setRenderMode(RENDERMODE_WHEN_DIRTY);
        setRenderMode(RENDERMODE_CONTINUOUSLY);

        //guideLine = (Guideline)findViewById(R.id.guideline);
        //guideLine.setGuidelinePercent(guideLinePercent);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels * (1.0f - guideLinePercent);
        screenWidth = displayMetrics.widthPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        platformPositionLock = true;
        MyGLRenderer.UpdatePlatformPosition(x);
        platformPositionLock = false;

        return true;
    }
}
