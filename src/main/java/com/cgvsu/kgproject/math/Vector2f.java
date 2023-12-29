package com.cgvsu.kgproject.math;
public final class Vector2f {
    public float x;
    public float y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float get(int index) {
        switch (index) {
            case 0:
                return x;
            case 1:
                return y;
            default:
                throw new IndexOutOfBoundsException("Index should be 0, 1, or 2 for Vector2f");
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2f vector2f = (Vector2f) o;
        return Float.compare(vector2f.x, x) == 0 && Float.compare(vector2f.y, y) == 0;
    }

    @Override
    public String toString() {
        return "Vector2 " +
                "x=" + x +
                ", y=" + y;
    }
}


