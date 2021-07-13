package com.example.brickbreaker_try_0;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import androidx.constraintlayout.widget.Guideline;

public class MyGLSurfaceView extends GLSurfaceView {
    MyGLRenderer myGLRenderer;
    private float previousX, previousY;
    public static float screenHeight, screenWidth;
    private float guideLinePercent = 0.1f;
    //private Guideline guideLine;
    private final int X = 0, Y = 1, Z = 2;

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

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = 2.0f * (x - previousX) / screenWidth;
                float dy = 2.0f * (y - previousY) / screenHeight;    // the height of MyGLSurface window is less (thus, different) than the screenHeight

                {
                    Shape platform = myGLRenderer.shapes.get("Platform");
                    float[] platformPosition = platform.GetShapePosition();
                    platform.SetShapePosition(new float[] {
                            MyMath.Clamp(2.0f * x / screenWidth - 1.0f, -1.0f, 1.0f),
                            platformPosition[Y],
                            0.0f
                    });
                }

                requestRender();
        }

        previousX = x;
        previousY = y;

        return true;
    }
}
