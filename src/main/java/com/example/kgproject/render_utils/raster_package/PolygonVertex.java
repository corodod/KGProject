package com.example.kgproject.render_utils.raster_package;
import com.example.kgproject.math.Vector2f;
import com.example.kgproject.math.Vector3f;

public class PolygonVertex {
    private final int x;
    private final int y;
    private final float z;

    private final Vector2f texture;
    private final Vector3f normal;

    private final Vector3f worldCoordinates;


    public PolygonVertex(int x, int y, float z, Vector2f texture, Vector3f normal, Vector3f worldCoordinates) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.texture = texture;
        this.normal = normal;
        this.worldCoordinates = worldCoordinates;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public Vector2f getTexture() {
        return texture;
    }

    public Vector3f getWorldCoordinates() {
        return worldCoordinates;
    }

    public boolean isScreenCoordinatesEquals(PolygonVertex vertex) {
        return this.x == vertex.x && this.y == vertex.y;
    }
}

