package com.snOwmOtion.brickbreaker_try_0;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Shape {
    enum Attribute {
        Position,
        Color
    }

    protected final int X = 0, Y = 1, Z = 2;

    VertexFormat[] vertices;
    short[] indices;
    public float[] modelMatrix = new float[16];
    public float[] viewMatrix = new float[16];
    public float[] orthoMatrix = new float[16];

    protected int positionHandle;
    protected int colorHandle;
    protected int gradientLineEquationHandle;
    protected int gradientLineColorHandle;
    protected int modelMatrixHandle;
    protected int viewMatrixHandle;
    protected int orthoMatrixHandle;

    FloatBuffer triangleVPFB;   // Vertex Position Float Buffer
    ByteBuffer triangleVPBB;    // Vertex Position Byte Buffer

    FloatBuffer triangleVCFB;   // Vertex Color Float Buffer
    ByteBuffer triangleVCBB;    // Vertex Color Byte Buffer

    ShortBuffer triangleISB;    // Indices Short Buffer
    ByteBuffer triangleIBB;     // Indices Byte Buffer

    public void UpdateBuffers() {
        triangleVPBB = ByteBuffer.allocateDirect(vertices.length * VertexFormat.POSITION_LENGTH * SizeOf.sizeOf(float.class));
        triangleVPBB.order(ByteOrder.nativeOrder());
        triangleVPFB = triangleVPBB.asFloatBuffer();
        triangleVPFB.put(GetAttribute(Attribute.Position));
        triangleVPFB.position(0);

        triangleVCBB = ByteBuffer.allocateDirect(vertices.length * VertexFormat.COLOR_LENGTH * SizeOf.sizeOf(float.class));
        triangleVCBB.order(ByteOrder.nativeOrder());
        triangleVCFB = triangleVCBB.asFloatBuffer();
        triangleVCFB.put(GetAttribute(Attribute.Color));
        triangleVCFB.position(0);

        triangleIBB = ByteBuffer.allocateDirect(indices.length * SizeOf.sizeOf(short.class));
        triangleIBB.order(ByteOrder.nativeOrder());
        triangleISB = triangleIBB.asShortBuffer();
        triangleISB.put(indices);
        triangleISB.position(0);
    }

    public Shape(VertexFormat[] vertices, short[] indices) {
        this.vertices = vertices;
        this.indices = indices;
        Matrix.setIdentityM(modelMatrix, 0);
        UpdateBuffers();
    }

    public float[] GetAttribute(Attribute attr) {
        if (attr == Shape.Attribute.Position) {
            float[] positions = new float[vertices.length * VertexFormat.POSITION_LENGTH];
            for (int i = 0; i < vertices.length; i++)
                for (int j = 0; j < VertexFormat.POSITION_LENGTH; j++)
                    positions[i * VertexFormat.POSITION_LENGTH + j] = vertices[i].position[j];

            return positions;
        }

        if (attr == Attribute.Color) {
            float[] colors = new float[vertices.length * VertexFormat.COLOR_LENGTH];
            for (int i = 0; i < vertices.length; i++)
                for (int j = 0; j < VertexFormat.COLOR_LENGTH; j++)
                    colors[i * VertexFormat.COLOR_LENGTH + j] = vertices[i].color[j];

            return colors;
        }

        return null;
    }

    public float[] GetShapePosition() {
        float[] currentVertexPosition = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        float[] finalPosition = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        for (int v = 0; v < vertices.length; v++) {
            Matrix.multiplyMV(currentVertexPosition, 0, modelMatrix, 0, vertices[v].position, 0);
            finalPosition[X] += currentVertexPosition[X] / vertices.length;
            finalPosition[Y] += currentVertexPosition[Y] / vertices.length;
            finalPosition[Z] += currentVertexPosition[Z] / vertices.length;
        }
        return finalPosition;
    }

    public void SetShapePosition(float[] newPosition) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, newPosition[X], newPosition[Y], newPosition[Z]);
    }

    public void draw(int shaderProgram, boolean draw3D, boolean enableBlending, boolean hasGradient) {
        GLES20.glUseProgram(shaderProgram);

        if (enableBlending) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
        }

        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, VertexFormat.POSITION_LENGTH, GLES20.GL_FLOAT, false,
                VertexFormat.POSITION_LENGTH * SizeOf.sizeOf(float.class), triangleVPFB);

        colorHandle = GLES20.glGetAttribLocation(shaderProgram, "vColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, VertexFormat.COLOR_LENGTH, GLES20.GL_FLOAT, false,
                VertexFormat.COLOR_LENGTH * SizeOf.sizeOf(float.class), triangleVCFB);

        modelMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "modelMatrix");
        GLES20.glUniformMatrix4fv(modelMatrixHandle, 1, false, modelMatrix, 0);

        if (draw3D) {
            viewMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "viewMatrix");
            GLES20.glUniformMatrix4fv(viewMatrixHandle, 1, false, MyGLRenderer.viewMatrix, 0);

            orthoMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "orthoMatrix");
            GLES20.glUniformMatrix4fv(orthoMatrixHandle, 1, false, MyGLRenderer.orthoMatrix, 0);
        }

        if (hasGradient) {
            gradientLineEquationHandle = GLES20.glGetUniformLocation(shaderProgram, "gradientLineEq");
            GLES20.glUniform3f(
                    gradientLineEquationHandle,
                    GameStatus.gradientLine1.equation[X],
                    GameStatus.gradientLine1.equation[Y],
                    GameStatus.gradientLine1.equation[Z]
            );

            gradientLineColorHandle = GLES20.glGetUniformLocation(shaderProgram, "gradientLineColor");
            GLES20.glUniform3f(
                    gradientLineColorHandle,
                    GameStatus.gradientLine1.color[0],
                    GameStatus.gradientLine1.color[1],
                    GameStatus.gradientLine1.color[2]
            );
        }

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, triangleISB.capacity(), GLES20.GL_UNSIGNED_SHORT, triangleISB);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);

        if (enableBlending)
            GLES20.glDisable(GLES20.GL_BLEND);
    }

    protected void ChangeShapeColor(float[] newColor) {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new VertexFormat(vertices[i].position, newColor);
        }
    }
}
