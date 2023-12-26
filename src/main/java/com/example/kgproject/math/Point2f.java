package com.example.kgproject.math;

public final class Point2f {
    public float x;
    public float y;

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.x * width + width / 2.0F, -vertex.y * height + height / 2.0F);
    }

    @Override
    public String toString() {
        return "Point2f{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

