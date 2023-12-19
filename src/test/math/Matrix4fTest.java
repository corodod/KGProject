package test.math;

import main.math.Matrix4f;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
//Junit 5.81

public class Matrix4fTest {
    @Test
    public void testDefaultConstructor() {
        Matrix4f matrix = new Matrix4f();
        assertEquals(0.0f, matrix.get(0, 0), 0.001);
        assertEquals(0.0f, matrix.get(1, 1), 0.001);
        assertEquals(0.0f, matrix.get(2, 2), 0.001);
        assertEquals(0.0f, matrix.get(3, 3), 0.001);
    }
    @Test
    public void testParameterizedConstructor() {
        Matrix4f matrix = new Matrix4f(1.0f, 2.0f, 3.0f, 4.0f,
                5.0f, 6.0f, 7.0f, 8.0f,
                9.0f, 10.0f, 11.0f, 12.0f,
                13.0f, 14.0f, 15.0f, 16.0f);
        assertEquals(1.0f, matrix.get(0, 0), 0.001);
        assertEquals(6.0f, matrix.get(1, 1), 0.001);
        assertEquals(11.0f, matrix.get(2, 2), 0.001);
        assertEquals(16.0f, matrix.get(3, 3), 0.001);
    }
    @Test
    public void testMultiplication() {
        Matrix4f matrix1 = new Matrix4f(1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12,
                13, 14, 15, 16);
        Matrix4f matrix2 = new Matrix4f(17, 18, 19, 20,
                21, 22, 23, 24,
                25, 26, 27, 28,
                29, 30, 31, 32);
        matrix1.mul(matrix2);
        assertEquals(250.0f, matrix1.get(0, 0), 0.001);
    }
}

