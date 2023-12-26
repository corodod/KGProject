package com.example.kgproject.scene;


import com.example.kgproject.math.Vector3f;
import com.example.kgproject.model.AffineModelTransformer;
import com.example.kgproject.render_utils.Camera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Scene {
    private HashMap<AffineModelTransformer, ModelSceneOptions> models;
    private AffineModelTransformer activeModel;

    private List<Camera> cameras;

    private int activeCameraIndex;

    private final Camera DEFAULT_CAMERA = new Camera(
            new Vector3f(0, 0, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    public Scene() {
        this.models = new HashMap<>();

        this.cameras = new ArrayList<>();
        this.cameras.add(DEFAULT_CAMERA);

        this.activeCameraIndex = 0;
    }

    private void addModel(AffineModelTransformer model, boolean isModelActive) {

        models.put(model, new ModelSceneOptions());
    }

    public void addModelToScene(AffineModelTransformer model) {
        addModel(model, false);
    }

    public void deleteModel(String modelName) {
        models.remove(getModel(modelName));
    }

    public void addActiveModelToScene(AffineModelTransformer model) {
        addModel(model, true);
    }

    public void deleteActiveModel(String modelName) {
        addModel(getModel(modelName), false);
    }

    public void addCamera(Camera camera) {
        cameras.add(camera);
    }

    public void deleteCamera(int index) {
        cameras.remove(index);

        if (index == activeCameraIndex) {
            activeCameraIndex = 0;
        }
    }

    public void setActiveCamera(int index) {
        activeCameraIndex = index;
    }

    public Camera getActiveCamera() {
        return cameras.get(activeCameraIndex);
    }

    public Set<AffineModelTransformer> getModels() {
        return models.keySet();
    }

    public List<Camera> getCameras() {
        return cameras;
    }

    public boolean isModelActive(AffineModelTransformer model) {
        return model.equals(activeModel);
    }

    public ModelSceneOptions getModelOptions(String modelName) {
        return models.get(getModel(modelName));
    }

    public void addModelSceneOptions(AffineModelTransformer model, ModelSceneOptions options) {
        models.put(model, options);
    }

    public AffineModelTransformer getModel(String modelName) {
        for (var model : models.keySet()) {
            if (model.getModelName().equals(modelName)) {
                return model;
            }
        }

        return null;
    }

    public AffineModelTransformer getActiveModel() {
        return activeModel;
    }

    public void setActiveModel(String modelName) {
        activeModel = getModel(modelName);
    }
}


