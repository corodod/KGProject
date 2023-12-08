package test.math;

import main.math.Vector3f;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Vector3fTest {
    @Test
    public void testDefaultConstructor() {
        Vector3f vector = new Vector3f();
        assertEquals(0.0f, vector.x, 0.001);
        assertEquals(0.0f, vector.y, 0.001);
        assertEquals(0.0f, vector.z, 0.001);
    }
    @Test
    public void testParameterizedConstructor() {
        Vector3f vector = new Vector3f(1.0f, 2.0f, 3.0f);
        assertEquals(1.0f, vector.x, 0.001);
        assertEquals(2.0f, vector.y, 0.001);
        assertEquals(3.0f, vector.z, 0.001);
    }
    @Test
    public void testSubtraction() {
        Vector3f vector1 = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f vector2 = new Vector3f(0.5f, 1.0f, 1.5f);
        Vector3f result = new Vector3f();

        result.sub(vector1, vector2);

        assertEquals(0.5f, result.x, 0.001);
        assertEquals(1.0f, result.y, 0.001);
        assertEquals(1.5f, result.z, 0.001);
    }
    @Test
    public void testCrossProduct() {
        Vector3f vector1 = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f vector2 = new Vector3f(0.5f, 1.0f, 1.5f);
        Vector3f result = new Vector3f();

        result.cross(vector1, vector2);

        assertEquals(0, result.x, 0.001);
        assertEquals(0, result.y, 0.001);
        assertEquals(0, result.z, 0.001);
    }
}

