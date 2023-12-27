package com.cgvsu.kgproject.render_utils;
import com.cgvsu.kgproject.math.Matrix4f;
import com.cgvsu.kgproject.math.Vector3f;

public class CoordinateTransformer {
    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0, 1.0F, 0));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        final var resultX = new Vector3f();
        final var resultY = new Vector3f();
        final var resultZ = new Vector3f();

        resultZ.sub(target, eye);
        resultX.cross(up, resultZ);
        resultY.cross(resultZ, resultX);

        resultX.normalize();
        resultY.normalize();
        resultZ.normalize();

        float[] matrix = new float[]{
                resultX.x, resultX.y, resultX.z, -resultX.dot(eye),
                resultY.x, resultY.y, resultY.z, -resultY.dot(eye),
                resultZ.x, resultZ.y, resultZ.z, -resultZ.dot(eye),
                0, 0, 0, 1};
        return new Matrix4f(matrix);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        final var result = new Matrix4f();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.set(0, 0, tangentMinusOnDegree / aspectRatio);
        result.set(1, 1, tangentMinusOnDegree);
        result.set(2, 2, (farPlane + nearPlane) / (farPlane - nearPlane));
        result.set(3, 2, 1);
        result.set(2, 3, 2 * (nearPlane * farPlane) / (nearPlane - farPlane));
        return result;
    }
}

