package com.snOwmOtion.brickbreaker_try_0;

import android.opengl.Matrix;

import java.util.ArrayList;

public class ShapeFactory {
    private static final int X = 0, Y = 1;

    public static Shape Create2DEquilateralPolygon(float radius, int edgesNumber, boolean adjustSizeByScreenRatio, float[] color) {
        VertexFormat[] vertices = new VertexFormat[edgesNumber + 2];
        short[] indices = new short[(edgesNumber + 1) * 3];
        float[] center = new float[]{0.0f, 0.0f, 0.0f}; // We ignore this when checking for collisions! (based on the consideration that origin is at (0,0,0))
        float screenRatio = GameStatus.glWindowHeight / GameStatus.glWindowWidth;
        float adjustX = adjustSizeByScreenRatio
                ? screenRatio
                : 1.0f;

        vertices[0] = new VertexFormat(center, color);
        int vIdx = 1;
        for (float angle = 0.0f; angle <= 360.0f; angle += 360.0f / (float)edgesNumber) {
            float angleRad = angle * MyMath.deg2Rad;
            vertices[vIdx++] = new VertexFormat(
                    new float[] {
                            (center[0] + (float)Math.cos(angleRad) * radius) * adjustX,
                            center[1] + (float)Math.sin(angleRad) * radius,
                            center[2]},
                    color
            );
        }

        for (int e = 0; e < edgesNumber; e++) {
            indices[3 * e] = 0;
            indices[3 * e + 1] = (short)(e + 1);
            indices[3 * e + 2] = (short)(e + 2);
        }

        return new Shape(vertices, indices);
    }

    // verific aici sa nu fi facut aceeasi greseala ca la functia de mai sus, anume cea cu un triunghi in plus la sectiune cu indices! (acum n-am chef sa ma uit pe ea)
    public static Shape Create3DBall(float radius, int edgesNumber, float[] color, int numberOfIterations) {
        ArrayList<VertexFormat> vertices = new ArrayList<VertexFormat>();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        float[] center = new float[]{0.0f, 0.0f, 0.0f};

        float[] transformV1 = new float[16];
        float[] transformV2 = new float[16];
        float[] transformV3 = new float[16];
        float[] transformV4 = new float[16];

        Matrix.setIdentityM(transformV1, 0);
        Matrix.setIdentityM(transformV2, 0);
        Matrix.setIdentityM(transformV3, 0);
        Matrix.setIdentityM(transformV4, 0);

        Matrix.rotateM(transformV1, 0, 30.0f, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(transformV1, 0, 30.0f, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(transformV1, 0, 1.0f, 0.0f, 0.0f);

        Matrix.rotateM(transformV2, 0, 30.0f, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(transformV2, 0, -150.0f, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(transformV2, 0, 1.0f, 0.0f, 0.0f);

        Matrix.rotateM(transformV3, 0, 30.0f, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(transformV3, 0, 90.0f, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(transformV3, 0, 1.0f, 0.0f, 0.0f);

        Matrix.rotateM(transformV4, 0, 90.0f, 0.0f, 0.0f, 1.0f);
        Matrix.translateM(transformV4, 0, 1.0f, 0.0f, 0.0f);

        float[] v1 = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        Matrix.multiplyMV(v1, 0, transformV1, 0, v1, 0);

        float[] v2 = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        Matrix.multiplyMV(v2, 0, transformV2, 0, v2, 0);

        float[] v3 = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        Matrix.multiplyMV(v3, 0, transformV3, 0, v3, 0);

        float[] v4 = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        Matrix.multiplyMV(v4, 0, transformV4, 0, v4, 0);

        /*System.out.println("v1 = " + v1[0] + ", " + v1[1] + ", " + v1[2] + ", " + v1[3]);
        System.out.println("norm = " + (v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]));*/

        /*v1 = new float[] {1.0f, -1.0f, -1.0f, 1.0f};
        v2 = new float[] {-1.0f, -1.0f, -1.0f, 1.0f};
        v3 = new float[] {0.0f, -1.0f, 1.0f, 1.0f};
        v4 = new float[] {0.0f, 1.0f, 0.0f, 1.0f};*/

        vertices.add(new VertexFormat(v1, color));
        vertices.add(new VertexFormat(v2, color));
        vertices.add(new VertexFormat(v3, color));
        vertices.add(new VertexFormat(v4, color));


        indices.add(3);    indices.add(0);    indices.add(2);
        indices.add(3);    indices.add(1);    indices.add(0);
        indices.add(3);    indices.add(2);    indices.add(1);
        indices.add(0);    indices.add(1);    indices.add(2);

        int totalVertices = vertices.size(); // 4

        for (int it = 0; it < numberOfIterations; it++) {
            int trianglesNumber = indices.size() / 3;
            for (int tri = 0; tri < trianglesNumber; tri++) {
                float[] vAux1 = vertices.get(indices.get(0)).position;
                float[] vAux2 = vertices.get(indices.get(1)).position;
                float[] vAux3 = vertices.get(indices.get(2)).position;
                int v1Idx = indices.get(0);
                int v2Idx = indices.get(1);
                int v3Idx = indices.get(2);

                indices.remove(0);
                indices.remove(0);
                indices.remove(0);

                float[] vAux4 = new float[] {
                        (vAux1[0] + vAux2[0] + vAux3[0]) / 3.0f,
                        (vAux1[1] + vAux2[1] + vAux3[1]) / 3.0f,
                        (vAux1[2] + vAux2[2] + vAux3[2]) / 3.0f,
                        1.0f
                };
                float vAux4Norm = (float)Math.sqrt(Math.pow(vAux4[0], 2) + Math.pow(vAux4[1], 2) + Math.pow(vAux4[2], 2));
                vAux4[0] /= vAux4Norm;
                vAux4[1] /= vAux4Norm;
                vAux4[2] /= vAux4Norm;

                int newVertexIdx = totalVertices++;
                vertices.add(new VertexFormat(vAux4, color));
                indices.add(newVertexIdx);  indices.add(v1Idx); indices.add(v2Idx);
                indices.add(newVertexIdx);  indices.add(v2Idx); indices.add(v3Idx);
                indices.add(newVertexIdx);  indices.add(v3Idx); indices.add(v1Idx);
            }
        }

        /*System.out.println("vertices :");
        for (int i = 0; i < vertices.size(); i++) {
            System.out.println("v[" + i + "] =" + vertices.get(i).position[0] + ", " + vertices.get(i).position[1] + ", " + vertices.get(i).position[2]);
        }

        System.out.println("indices = ");
        for (int i = 0; i < indices.size(); i += 3) {
            System.out.println("i[" + (i / 3) + "] =" + indices.get(i) + ", " + indices.get(i + 1) + ", " + indices.get(i + 2));
        }*/

        //VertexFormat[] vs = ConvertListToArray(vertices);
        //short[] iss = ConvertIntToShortArray(indices);
        //System.out.println("vs length = " + vs.length + " and iss length = " + iss.length);
        //Shape2D s = new Shape2D(ConvertListToArray(vertices), ConvertIntToShortArray(indices));

        return new Shape(ConvertListToArray(vertices), ConvertIntToShortArray(indices));
    }

    public static Brick CreateBrick(float[] edge, float[] color, int index) {
        Shape rectangle = CreateRectangle(edge[X], edge[Y], false, color);
        return new Brick(rectangle.vertices, rectangle.indices, index);
    }

    public static Powerup CreatePowerup(Powerup.FunctionalType functionalType, Powerup.ShapeType shapeType, float edgeSize, float[] color, int index) {
        // no need to adjust X size by the screen ratio, because of the adjusting scaling transform applied during the powerup's Update() method
        Shape powerupShape;
        switch (shapeType) {
            case POLYGON:
                powerupShape = Create2DEquilateralPolygon(ValueSheet.powerupPolygonEdge, 5, false, color);
                break;
            case SQUARE:
                powerupShape = CreateRectangle(0.1f, ValueSheet.powerupSquareEdge, false, color);
                break;
            default:    // case TRIANGLE:
                powerupShape = CreateEquilateralTriangle(ValueSheet.powerupTriangleEdge, false, color);
        }
        //Shape rectangle = CreateRectangle(edgeSize, edgeSize, false, color);
        return new Powerup(powerupShape.vertices, powerupShape.indices, functionalType, shapeType, index, color);
    }

    public static Shape CreateRectangle(float sizeX, float sizeY, boolean adjustSizeByScreenRatio, float[] color) {
        float adjustedSizeX = sizeX;
        if (adjustSizeByScreenRatio) {
            float screenRatio = GameStatus.glWindowHeight / GameStatus.glWindowWidth;
            adjustedSizeX *= screenRatio;
        }

        VertexFormat[] vertices = new VertexFormat[]{
                new VertexFormat(new float[]{-adjustedSizeX / 2.0f, -sizeY / 2.0f, 0}, color),
                new VertexFormat(new float[]{-adjustedSizeX / 2.0f, sizeY / 2.0f, 0}, color),
                new VertexFormat(new float[]{adjustedSizeX / 2.0f, -sizeY / 2.0f, 0}, color),
                new VertexFormat(new float[]{adjustedSizeX / 2.0f, sizeY / 2.0f, 0}, color),
        };
        short[] indices = new short[]{
                0, 2, 1, 1, 2, 3
        };

        return new Shape(vertices, indices);
    }

    public static Shape CreateEquilateralTriangle(float edge, boolean adjustSizeByScreenRatio, float color[]) {
        float triangleHeight = edge * (float)Math.sqrt(3.0f) / 2.0f;
        float screenRatio = GameStatus.glWindowHeight / GameStatus.glWindowWidth;
        float adjustedX = adjustSizeByScreenRatio
                ? screenRatio * edge
                : edge;

        VertexFormat[] vertices = new VertexFormat[]{
                new VertexFormat(new float[] {0.0f, triangleHeight * 2.0f / 3.0f, 0}, color),
                new VertexFormat(new float[] {adjustedX / 2.0f, -triangleHeight / 3.0f, 0}, color),
                new VertexFormat(new float[] {-adjustedX / 2.0f, -triangleHeight / 3.0f, 0}, color)
        };
        short[] indices = new short[]{
                0, 2, 1
        };

        return new Shape(vertices, indices);
    }

    private static VertexFormat[] ConvertListToArray(ArrayList<VertexFormat> al) {
        VertexFormat[] array = new VertexFormat[al.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = al.get(i);
        }
        return array;
    }

    private static short[] ConvertIntToShortArray(ArrayList<Integer> integerList) {
        short[] shortArray = new short[integerList.size()];
        for (int i = 0; i < shortArray.length; i++) {
            shortArray[i] = integerList.get(i).shortValue();
        }
        return shortArray;
    }
}
