package main.math;
import java.util.Arrays;

public final class Matrix4f {
    private float[][] matrix;
    public Matrix4f() {
        this.matrix = new float[4][4];
    }

    public Matrix4f(float m00, float m01, float m02, float m03,
                    float m10, float m11, float m12, float m13,
                    float m20, float m21, float m22, float m23,
                    float m30, float m31, float m32, float m33) {
        this.matrix = new float[4][4];
        this.matrix[0][0] = m00;
        this.matrix[0][1] = m01;
        this.matrix[0][2] = m02;
        this.matrix[0][3] = m03;

        this.matrix[1][0] = m10;
        this.matrix[1][1] = m11;
        this.matrix[1][2] = m12;
        this.matrix[1][3] = m13;

        this.matrix[2][0] = m20;
        this.matrix[2][1] = m21;
        this.matrix[2][2] = m22;
        this.matrix[2][3] = m23;

        this.matrix[3][0] = m30;
        this.matrix[3][1] = m31;
        this.matrix[3][2] = m32;
        this.matrix[3][3] = m33;
    }
    public Matrix4f(Matrix4f matrix4f) {
        this(matrix4f.matrix[0][0], matrix4f.matrix[0][1], matrix4f.matrix[0][2], matrix4f.matrix[0][3],
                matrix4f.matrix[1][0], matrix4f.matrix[1][1], matrix4f.matrix[1][2], matrix4f.matrix[1][3],
                matrix4f.matrix[2][0], matrix4f.matrix[2][1], matrix4f.matrix[2][2], matrix4f.matrix[2][3],
                matrix4f.matrix[3][0], matrix4f.matrix[3][1], matrix4f.matrix[3][2], matrix4f.matrix[3][3]);
    }
    public Matrix4f(float[] vertices) {
        if (vertices == null) {
            throw new IllegalStateException("v is null");
        }

        if (vertices.length < 16) {
            throw new IllegalArgumentException("Can not create a Matrix 4x4 with that length = " + vertices.length);
        }
        this.matrix = new float[4][4];

        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(vertices, 4 * i, matrix[i], 0, matrix.length);
        }
    }
    public void mul(Matrix4f m1) {
        var temp = new float[4][4];
        for (int i = 0; i < matrix.length; i++) {
            for (int u = 0; u < matrix[i].length; u++) {
                for (int j = 0; j < matrix.length; j++) {
                    temp[i][u] += this.matrix[i][j] * m1.matrix[j][u];
                }
            }
        }

        this.matrix = temp;
    }
    public Vector3f multiplyByVector3(final Vector3f vertex) {
        final var x = (vertex.x * this.matrix[0][0]) + (vertex.y * this.matrix[0][1]) + (vertex.z * this.matrix[0][2]) + this.matrix[0][3];
        final var y = (vertex.x * this.matrix[1][0]) + (vertex.y * this.matrix[1][1]) + (vertex.z * this.matrix[1][2]) + this.matrix[1][3];
        final var z = (vertex.x * this.matrix[2][0]) + (vertex.y * this.matrix[2][1]) + (vertex.z * this.matrix[2][2]) + this.matrix[2][3];
        var w = (vertex.x * this.matrix[3][0]) + (vertex.y * this.matrix[3][1]) + (vertex.z * this.matrix[3][2]) + this.matrix[3][3];

        if (MathMethods.isEqual(w, 0)) {
            w = 1;
        }
        return new Vector3f(x / w, y / w, z / w);
    }
    public static Matrix4f identityMatrix() {
        float[] matrix = new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1};
        return new Matrix4f(matrix);
    }
    public static Matrix4f rotationMatrix(float angleX,
                                          float angleY,
                                          float angleZ) {
        float[] matrix = new float[]{
                (float) (Math.cos(angleY) * Math.cos(angleZ)), (float) (-Math.sin(angleZ) * Math.cos(angleY)), (float) Math.sin(angleY), 0,
                (float) (Math.sin(angleX) * Math.sin(angleY) * Math.cos(angleZ) + Math.sin(angleZ) * Math.cos(angleX)), (float) (-Math.sin(angleX) * Math.sin(angleY) * Math.sin(angleZ) + Math.cos(angleZ) * Math.cos(angleX)), (float) (-Math.sin(angleX) * Math.cos(angleY)), 0,
                (float) (Math.sin(angleZ) * Math.sin(angleX) - Math.cos(angleX) * Math.sin(angleY) * Math.cos(angleZ)), (float) (Math.cos(angleZ) * Math.sin(angleX) + Math.cos(angleX) * Math.sin(angleY) * Math.sin(angleZ)), (float) (Math.cos(angleX) * Math.cos(angleY)), 0,
                0, 0, 0, 1
        };
        return new Matrix4f(matrix);
    }

    public static Matrix4f scaleMatrix(float scaleX, float scaleY, float scaleZ) {
        float[] matrix = new float[]{
                scaleX, 0, 0, 0,
                0, scaleY, 0, 0,
                0, 0, scaleZ, 0,
                0, 0, 0, 1
        };
        return new Matrix4f(matrix);
    }

    public static Matrix4f translationMatrix(float translateX, float translateY, float translateZ) {
        float[] matrix = new float[]{
                1, 0, 0, translateX,
                0, 1, 0, translateY,
                0, 0, 1, translateZ,
                0, 0, 0, 1
        };
        return new Matrix4f(matrix);
    }

    public Matrix4f(float[][] matrix) {//дополнительный конструктор
        if (matrix == null || matrix.length != 4 || matrix[0].length != 4 || matrix[1].length != 4 || matrix[2].length != 4 || matrix[3].length != 4) {
            throw new IllegalArgumentException("Invalid matrix dimensions");
        }
        this.matrix = matrix;
    }
    public Matrix4f transpose() {
        float[][] transposed = new float[4][4];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                transposed[i][j] = matrix[j][i];
            }
        }
        return new Matrix4f(transposed);
    }
    public float get(int n, int m) {
        return matrix[n][m];
    }
    public void set(int n, int m, float value) {
        this.matrix[n][m] = value;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (var array : matrix)
            sb.append(Arrays.toString(array));
        return "matrix=" + sb;
    }
}
