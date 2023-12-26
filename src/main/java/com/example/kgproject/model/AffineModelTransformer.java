package com.example.kgproject.model;

import com.example.kgproject.math.Matrix4f;
import com.example.kgproject.math.Vector2f;
import com.example.kgproject.math.Vector3f;
import com.example.kgproject.render_utils.raster_package.RenderingPreparationClass;

import java.util.ArrayList;
import java.util.List;

public class AffineModelTransformer {
    private Model initialModel;
    private final List<Polygon> triangulatedPolygons;

    private Vector3f translatedVector;
    private float rotateAngleX;
    private float rotateAngleY;
    private float rotateAngleZ;
    private float scaleX;
    private float scaleY;
    private float scaleZ;

    public AffineModelTransformer(Model model) {
        if (model == null) {
            throw new IllegalArgumentException("Can't create a transformed model, because source is null");
        }

        this.initialModel = model;
        this.triangulatedPolygons = triangulateAndRecalculateNormals(model);
        this.translatedVector = new Vector3f();
        this.scaleX = 1;
        this.scaleY = 1;
        this.scaleZ = 1;
    }

    public String getModelName() {
        return initialModel.modelName;
    }

    public Model getModel() {
        return initialModel;
    }


    public List<Polygon> getPolygons() {
        return triangulatedPolygons;
    }

    public List<Vector3f> getNormals() {
        return initialModel.normals;
    }

    public List<Vector2f> getTexture() {
        return initialModel.textureVertices;
    }

    public void setRotate(Vector3f rotateVector) {
        this.rotateAngleX = (float) Math.toRadians(rotateVector.x);
        this.rotateAngleY = (float) Math.toRadians(rotateVector.y);
        this.rotateAngleZ = (float) Math.toRadians(rotateVector.z);
    }

    public void setScale(Vector3f scaleVector) {
        this.scaleX = scaleVector.x;
        this.scaleY = scaleVector.y;
        this.scaleZ = scaleVector.z;
    }

    public void setTranslatedVector(Vector3f translatedVector) {
        this.translatedVector = translatedVector;
    }

    public Vector3f getTransformedVector(int index) {
        final var defaultVector = initialModel.vertices.get(index);
        var transformedVector = new Vector3f(defaultVector.x, defaultVector.y, defaultVector.z);
        transformedVector.scaleX(scaleX);
        transformedVector.scaleY(scaleY);
        transformedVector.scaleZ(scaleZ);
        transformedVector = getRotationMatrix().multiplyByVector3(transformedVector);
        transformedVector.add(translatedVector);
        return transformedVector;
    }

    public void setModel(Model model) {
        this.initialModel = model;
    }


    private List<Polygon> triangulateAndRecalculateNormals(Model model) {
        List<Polygon> triangulatedPolygons = new ArrayList<>();

        for (Polygon polygon : model.polygons) {
            triangulatedPolygons.addAll(RenderingPreparationClass.triangulation(polygon));
        }

        RenderingPreparationClass.recalculateNormals(model);

        return triangulatedPolygons;
    }

    public Matrix4f getRotationMatrix() {
        return Matrix4f.rotationMatrix(rotateAngleX, rotateAngleY, rotateAngleZ);
    }

    public Model getTriangulatedModel() {
        return initialModel;
    }
}

