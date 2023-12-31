package com.cgvsu.kgproject.math;
import java.util.List;
import java.util.Objects;

public final class Vector3f {
    public float x;
    public float y;
    public float z;

    public Vector3f() {
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3f vector3f) {
        this(vector3f.x, vector3f.y, vector3f.z);
    }

    public void sub(Vector3f to, Vector3f from) {
        if (to == null || from == null) {
            throw new IllegalArgumentException("Vector3f can not be null");
        }
        this.x = to.x - from.x;
        this.y = to.y - from.y;
        this.z = to.z - from.z;
    }

    public void cross(Vector3f v1, Vector3f v2) {
        if (v1 == null || v2 == null) {
            throw new IllegalArgumentException("Vector3f can not be null");
        }

        float x = v1.y * v2.z - v1.z * v2.y;
        float y = v2.x * v1.z - v2.z * v1.x;

        this.z = v1.x * v2.y - v1.y * v2.x;
        this.x = x;
        this.y = y;
    }

    public void normalize() {
        float norm = (float) (1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z));
        if (MathMethods.isEqual(length(), 0)) {
            norm = 0;
        }
        this.x *= norm;
        this.y *= norm;
        this.z *= norm;

    }

    public float dot(Vector3f v1) {
        if (v1 == null) {
            throw new IllegalArgumentException("Vector3f can not be null");
        }
        return this.x * v1.x + this.y * v1.y + this.z * v1.z;
    }

    public void add(Vector3f t1, Vector3f t2) {
        if (t1 == null || t2 == null) {
            throw new IllegalArgumentException("Vector3f can not be null");
        }

        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
        this.z = t1.z + t2.z;
    }

    public void add(Vector3f t1) {
        if (t1 == null) {
            throw new IllegalArgumentException("Vector3f can not be null");
        }

        this.x += t1.x;
        this.y += t1.y;
        this.z += t1.z;
    }

    public void scale(Vector3f scale) {
        this.x *= scale.x;
        this.y *= scale.y;
        this.z *= scale.z;
    }

    public static Vector3f fromTwoPoints(Vector3f vertex1, Vector3f vertex2) {
        return new Vector3f(vertex2.x - vertex1.x,
                vertex2.y - vertex1.y,
                vertex2.z - vertex1.z);
    }

    public static Vector3f sum(List<Vector3f> vectors) {
        final var result = new Vector3f();

        vectors.forEach(result::add);

        return result;
    }

    public float length() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    public Vector3f divide(float num) {
        if (MathMethods.isEqual(num, 0))
            throw new ArithmeticException("Division by zero");

        return new Vector3f(x / num, y / num, z / num);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3f vector3f = (Vector3f) o;
        return Float.compare(vector3f.x, x) == 0 &&
                Float.compare(vector3f.y, y) == 0 &&
                Float.compare(vector3f.z, z) == 0;
    }

    @Override
    public String toString() {
        return "Vector3f{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}


