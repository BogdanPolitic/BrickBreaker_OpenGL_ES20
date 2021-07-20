package com.snOwmOtion.brickbreaker_try_0;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {
    public static MyGLSurfaceView instance;
    MyGLRenderer myGLRenderer;
    public static float screenHeight, screenWidth;
    private float guideLinePercent = 0.1f;
    //private Guideline guideLine;
    private final int X = 0, Y = 1, Z = 2;
    public static boolean platformPositionLock = false;
    public boolean gameInteractionEnabled = true;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        instance = this;

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

    public static MyGLSurfaceView GetInstance() {
        return instance;
    }

    public void SetGameInteraction(boolean gameInteraction) {
        gameInteractionEnabled = gameInteraction;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        if (gameInteractionEnabled) {
            platformPositionLock = true;
            MyGLRenderer.UpdatePlatformPosition(x);
            platformPositionLock = false;
        }

        return true;
    }
}
