package com.example.brickbreaker_try_0;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import android.content.Context;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.renderscript.Byte4;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private static HashMap<String, Integer> shaderPrograms = new HashMap<String, Integer>();
    public static HashMap<String, Shape> shapes = new HashMap<String, Shape>();
    public static float[] orthoMatrix = new float[16];
    public static float[] viewMatrix = new float[16];
    private final int X = 0;
    private final int Y = 1;

    private Context context;

    private final String simple2DShapeVS =
                    "attribute vec3 vPosition;" +
                    "attribute vec4 vColor;" +
                    "uniform mat4 modelMatrix;" +
                    "varying vec4 outColor;" +
                    "void main() {" +
                        "outColor = vColor;" +
                        "gl_Position = modelMatrix * vec4(vPosition, 1);" +
                    "}";
    private final String simple2DShapeFS =
                    "varying vec4 outColor;" +
                    "void main() {" +
                        "gl_FragColor = outColor;" +
                    "}";

    private final String ball3DShapeVS =
            "attribute vec3 vPosition;" +
                    "attribute vec4 vColor;" +
                    "uniform mat4 modelMatrix;" +
                    "uniform mat4 viewMatrix;" +
                    "uniform mat4 orthoMatrix;" +
                    "varying vec4 outColor;" +
                    "void main() {" +
                        "outColor = vColor;" +
                        "gl_Position = orthoMatrix * viewMatrix * modelMatrix * vec4(vPosition, 1);" +
                        //"gl_Position = modelMatrix * vec4(vPosition, 1);" +
                    "}";
    private final String ball3DShapeFS =
                    "varying vec4 outColor;" +
                    "void main() {" +
                        "gl_FragColor = outColor;" +
                    "}";

    public MyGLRenderer(Context context) {
        super();
        this.context = context;
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static int createShaderProgram(int vertexShader, int fragmentShader) {
        int shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);

        return shaderProgram;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 1, 1);

        Matrix.setLookAtM(viewMatrix, 0, 0.0f, 0.0f, -1.5f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.orthoM(orthoMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, 0.001f, 3.0f);

        {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, simple2DShapeVS);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, simple2DShapeFS);
            shaderPrograms.put("Simple2DShape", createShaderProgram(vertexShader, fragmentShader));
        }

        {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, ball3DShapeVS);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, ball3DShapeFS);
            shaderPrograms.put("Ball3DShape", createShaderProgram(vertexShader, fragmentShader));
        }

        Collisions.Init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {  // primul onSurfaceChanged are call-ul cam cu 2ms (nesemnificativ) mai tarziu decat onSurfaceCreated
        GLES20.glViewport(0, 0, width, height);
        GameStatus.OnWindowChanged((float)width, (float)height);

        {
            float[] colorTop = new float[]{1, 0, 0, 1};
            float[] colorBottom = new float[]{1, 1, 0, 1};

            VertexFormat[] vertices = new VertexFormat[]{
                    new VertexFormat(new float[]{-1.0f, -1.0f + ValueSheet.sideBordersOffsetFromGround, 0}, colorBottom),
                    new VertexFormat(new float[]{-1.0f, 1.0f, 0}, colorTop),
                    new VertexFormat(new float[]{-1.0f + ValueSheet.borderWidthHoriz, -1.0f + ValueSheet.sideBordersOffsetFromGround, 0}, colorBottom),
                    new VertexFormat(new float[]{-1.0f + ValueSheet.borderWidthHoriz, 1.0f, 0}, colorTop),
            };
            short[] indices = new short[]{
                    0, 2, 1, 1, 2, 3
            };
            shapes.put("BorderLeft", new Shape(vertices, indices));
        }

        {
            float[] colorTop = new float[]{1, 0, 0, 1};
            float[] colorBottom = new float[]{1, 1, 0, 1};

            VertexFormat[] vertices = new VertexFormat[]{
                    new VertexFormat(new float[]{1.0f - ValueSheet.borderWidthHoriz, -1.0f + ValueSheet.sideBordersOffsetFromGround, 0}, colorBottom),
                    new VertexFormat(new float[]{1.0f - ValueSheet.borderWidthHoriz, 1.0f, 0}, colorTop),
                    new VertexFormat(new float[]{1.0f, -1.0f + ValueSheet.sideBordersOffsetFromGround, 0}, colorBottom),
                    new VertexFormat(new float[]{1.0f, 1.0f, 0}, colorTop),
            };
            short[] indices = new short[]{
                    0, 2, 1, 1, 2, 3
            };
            shapes.put("BorderRight", new Shape(vertices, indices));
        }

        {
            float borderWidthVert = ValueSheet.borderWidthHoriz * width / height;
            float[] color = new float[]{1, 0, 0, 1};

            VertexFormat[] vertices = new VertexFormat[]{
                    new VertexFormat(new float[]{-1.0f + ValueSheet.borderWidthHoriz, 1.0f - borderWidthVert, 0}, color),
                    new VertexFormat(new float[]{-1.0f + ValueSheet.borderWidthHoriz, 1.0f, 0}, color),
                    new VertexFormat(new float[]{1.0f - ValueSheet.borderWidthHoriz, 1.0f - borderWidthVert, 0}, color),
                    new VertexFormat(new float[]{1.0f - ValueSheet.borderWidthHoriz, 1.0f, 0}, color),
            };
            short[] indices = new short[]{
                    0, 2, 1, 1, 2, 3
            };
            shapes.put("BorderTop", new Shape(vertices, indices));
        }

        {
            float[] color = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
            VertexFormat[] vertices = new VertexFormat[]{
                    new VertexFormat(new float[]{-1.0f, -1.0f, 0}, color),
                    new VertexFormat(new float[]{-1.0f, -1.0f + ValueSheet.groundBorderHeight, 0}, color),
                    new VertexFormat(new float[]{1.0f, -1.0f, 0}, color),
                    new VertexFormat(new float[]{1.0f, -1.0f + ValueSheet.groundBorderHeight, 0}, color),
            };
            short[] indices = new short[]{
                    0, 2, 1, 1, 2, 3
            };
            shapes.put("BorderGround", new Shape(vertices, indices));
        }


        {
            float[] color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
            VertexFormat[] vertices = new VertexFormat[]{
                    new VertexFormat(new float[]{-ValueSheet.platformWidth / 2.0f, -ValueSheet.platformHeight / 2.0f, 0}, color),
                    new VertexFormat(new float[]{-ValueSheet.platformWidth / 2.0f, ValueSheet.platformHeight / 2.0f, 0}, color),
                    new VertexFormat(new float[]{ValueSheet.platformWidth / 2.0f, -ValueSheet.platformHeight / 2.0f, 0}, color),
                    new VertexFormat(new float[]{ValueSheet.platformWidth / 2.0f, ValueSheet.platformHeight / 2.0f, 0}, color),
            };
            short[] indices = new short[]{
                    0, 2, 1, 1, 2, 3
            };
            Platform platform = new Platform(vertices, indices);
            shapes.put("Platform", platform);
        }

        {
            for (int ballId = 0; ballId < GameStatus.remainingBallsCount; ballId++) {
                Shape ballShape = ShapeFactory.Create2DBall(ValueSheet.ballRadius, ValueSheet.ballEdgesNumber, new float[]{1, 0, 1, 1});
                shapes.put("Ball2D" + ballId,
                        new Ball2D(
                                ballShape.vertices,
                                ballShape.indices,
                                new float[]{-0.97f + 0.06f * ballId, -1.0f + ValueSheet.groundBorderHeight - 0.07f, 0},
                                new float[]{0, 0, 0}
                                )
                );
                Shape ball3DShape = ShapeFactory.Create3DBall(1.0f, 3, new float[]{1.0f, 0.0f, 1.0f, 1.0f}, 1);
                shapes.put("Ball3D", new Ball3D(
                        ball3DShape.vertices,
                        ball3DShape.indices,
                        new float[]{-0.95f * 0, -1.0f + ValueSheet.groundBorderHeight + 0.05f, 0},
                        new float[]{0, 0, 0})
                );
            }

            Shape ballShape = ShapeFactory.Create2DBall(
                    ValueSheet.ballRadius,
                    ValueSheet.ballEdgesNumber,
                    new float[]{1, 0, 1, 1}
                    );
            Ball2D activeBall = new Ball2D(
                    ballShape.vertices,
                    ballShape.indices,
                    shapes.get("Platform").GetShapePosition(),
                    new float[]{0, 0, 0}
                    );
            shapes.put("Ball2D" + GameStatus.ActiveBallId(), activeBall);
        }

        for (int brickIdx = 0; brickIdx < BrickNetwork.size[X] * BrickNetwork.size[Y]; brickIdx++) {
            Brick brick = ShapeFactory.CreateBrick(
                    new float[]{BrickNetwork.brickSize[X], BrickNetwork.brickSize[Y]},
                    new float[]{1.0f, 0.0f, 0.0f, 1.0f}, brickIdx
            );
            shapes.put("Brick" + brickIdx, brick);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GameStatus.UpdateTime();
        //GameStatus.NormalizeDirection();

        shapes.get("BorderLeft").draw(shaderPrograms.get("Simple2DShape"), false);
        shapes.get("BorderRight").draw(shaderPrograms.get("Simple2DShape"), false);
        shapes.get("BorderTop").draw(shaderPrograms.get("Simple2DShape"), false);
        shapes.get("BorderGround").draw(shaderPrograms.get("Simple2DShape"), false);
        shapes.get("Platform").draw(shaderPrograms.get("Simple2DShape"), false);
        //shapes.get("Ball3D").draw(shaderPrograms.get("Ball3DShape"), true);

        for (int ballId = 0; ballId < GameStatus.remainingBallsCount; ballId++) {
            shapes.get("Ball2D" + ballId).draw(shaderPrograms.get("Simple2DShape"), false);
        }

        {
            Shape activeBall = shapes.get("Ball2D" + GameStatus.ActiveBallId());

            switch (GameStatus.ballStatus) {
                case ON_PLATFORM:
                    activeBall.SetShapePosition(shapes.get("Platform").GetShapePosition());
                    Matrix.translateM(activeBall.modelMatrix, 0, 0.0f, ValueSheet.ballHeightRelativeToPlatform, 0.0f);
                    break;
                case FLOATING:
                    ((Ball2D)activeBall).Travel();
                    if (activeBall.GetShapePosition()[Y] < ValueSheet.platformPositionY - ValueSheet.depthUnderPlatformConsideredLost)
                        GameStatus.ballStatus = GameStatus.BallStatus.LOST;
                    Collisions.CheckForBorderCollisions();
                    Collisions.ApplyBorderCollisions();
                    break;
                default:
                    GameStatus.UnloadCurrentAndLoadNextBall();
            }

            if (GameStatus.ballStatus != GameStatus.BallStatus.LOST)
                activeBall.draw(shaderPrograms.get("Simple2DShape"), false);

            Collisions.CheckForBrickCollisions();
            Collisions.CheckForBrickCornerCollisions();
            Collisions.CheckForBrickStraightEdgeCollisions();
            Collisions.ApplyBrickCollisions();
        }

        for (int brickId = 0; brickId < BrickNetwork.size[X] * BrickNetwork.size[Y]; brickId++) {
            Brick brick = (Brick)shapes.get("Brick" + brickId);
            if (brick.status != Brick.Status.OFF) {
                brick.draw(shaderPrograms.get("Simple2DShape"), false);
                brick.Update();
            }
        }
    }
}
