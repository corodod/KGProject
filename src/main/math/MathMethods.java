package main.math;

public class MathMethods {
    public static final double EPS = 1e-5f;

    public static boolean isEqual(float x, float y){
        return Math.abs(x-y) < EPS;
    }
}
