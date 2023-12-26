package com.example.kgproject;


import com.example.kgproject.render_utils.RenderEngine;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.kgproject.math.Vector3f;
import com.example.kgproject.model.AffineModelTransformer;
import com.example.kgproject.model.Model;
import com.example.kgproject.model.RemovePolygons;
import com.example.kgproject.model.RemoveVertices;
import com.example.kgproject.objreader.ObjReader;
import com.example.kgproject.objreader.ObjReaderException;
import com.example.kgproject.objwriter.ObjWriter;
import com.example.kgproject.render_utils.Camera;
import com.example.kgproject.render_utils.MovementVector;
import com.example.kgproject.scene.ModelSceneOptions;
import com.example.kgproject.scene.Scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;



public class GuiController {

    final private float TRANSLATION = 1.0F;
    final private float SENSITIVITY = 0.04F;
    @FXML
    public TextField xRotateField;
    @FXML
    public TextField yRotateField;
    @FXML
    public TextField zRotateField;
    @FXML
    public TextField xScale;
    @FXML
    public TextField yScale;
    @FXML
    public TextField zScale;
    @FXML
    public TextField translateX;
    @FXML
    public TextField translateY;
    @FXML
    public TextField translateZ;

    @FXML
    private TextField newCameraX;
    @FXML
    private TextField newCameraY;
    @FXML
    private TextField newCameraZ;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    @FXML
    private ComboBox<String> activeCameraComboBox;
    @FXML
    public ComboBox<String> activeModelComboBox;
    @FXML
    private ComboBox<String> modelComboBox;

    @FXML
    private Label textureLabel;

    @FXML
    private CheckBox checkPolygonGridBox;
    @FXML
    private CheckBox checkTextureBox;
    @FXML
    private CheckBox checkUseLightBox;
    @FXML
    public TextField idVertic;
    @FXML
    public TextField idPolygon;

    private Timeline timeline;

    private Scene scene;

    private Image alertIcon;

    private float[][] zBuffer;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        scene = new Scene();

        fillCameras();
        fillModels();
        fillActiveModel();

        setTextureName();

        setCheckBoxesIndeterminate();

        alertIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/alert_icon.jpg")));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(80), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);

            scene.getActiveCamera().setAspectRatio((float) (width / height));

            zBuffer = new float[(int) height][(int) width];

            for (float[] array : zBuffer) {
                Arrays.fill(array, Float.MAX_VALUE);
            }

            for (var mesh : scene.getModels()) {
                if (mesh != null) {
                    renderMesh(mesh);
                }
            }
        });

        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    private double mouseX, mouseY;

    private void handleMousePressed(MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
    }

    private void handleMouseDragged(MouseEvent event) {
        double deltaX = event.getX() - mouseX;
        double deltaY = event.getY() - mouseY;

        double sensitivity = 0.01;

        scene.getActiveCamera().rotateCameraHorizontal((float) (deltaX * SENSITIVITY));
        scene.getActiveCamera().rotateCameraVertical((float) (deltaY * SENSITIVITY));

        mouseX = event.getX();
        mouseY = event.getY();
    }


    private void renderMesh(AffineModelTransformer model) {
        ModelSceneOptions options = scene.getModelOptions(model.getModelName());

        RenderEngine.render(canvas.getGraphicsContext2D(),
                scene.getActiveCamera(),
                model,
                (int) canvas.getWidth(),
                (int) canvas.getHeight(),
                zBuffer,
                options.texture,
                options.usedPolygonalGrid,
                options.usedTexture,
                options.usedLighting
        );
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            String objName = String.valueOf(fileName.getFileName());

            Model model = ObjReader.read(fileContent);
            if (scene.getModel(objName) != null) {
                String newObjName = objName;
                int num = 1;
                while (scene.getModel(newObjName) != null) {
                    newObjName = String.valueOf(num).concat(objName);
                    num++;
                }
                objName = newObjName;
            }

            model.modelName = objName;
            scene.addActiveModelToScene(new AffineModelTransformer(model));
            modelComboBox.getItems().add(objName);
            activeModelComboBox.getItems().add(objName);
        } catch (ObjReaderException exception) {
            showAlert(exception.getMessage());
        } catch (IOException exception) {
            showAlert("This file cannot be read.");
        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        scene.getActiveCamera().movePosition(MovementVector.FORWARD, TRANSLATION);
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        scene.getActiveCamera().movePosition(MovementVector.BACKWARD, TRANSLATION);
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        scene.getActiveCamera().movePosition(MovementVector.LEFT, TRANSLATION);
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        scene.getActiveCamera().movePosition(MovementVector.RIGHT, TRANSLATION);
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        scene.getActiveCamera().movePosition(MovementVector.UP, TRANSLATION);
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        scene.getActiveCamera().movePosition(MovementVector.DOWN, TRANSLATION);
    }

    @FXML
    public void rotateCameraAroundXLeft(ActionEvent actionEvent) {
        scene.getActiveCamera().rotateCameraHorizontal(TRANSLATION);
    }

    @FXML
    public void rotateCameraAroundXRight(ActionEvent actionEvent) {
        scene.getActiveCamera().rotateCameraHorizontal(-TRANSLATION);
    }

    @FXML
    public void rotateCameraAroundXUp(ActionEvent actionEvent) {
        scene.getActiveCamera().rotateCameraVertical(TRANSLATION);
    }

    @FXML
    public void rotateCameraAroundXDown(ActionEvent actionEvent) {
        scene.getActiveCamera().rotateCameraVertical(-TRANSLATION);
    }

    private boolean checkFieldsValues(String x, String y, String z) {
        if (x.equals("") || y.equals("") || z.equals("")) {
            return true;
        }

        return x.equals("-") || y.equals("-") || z.equals("-");
    }

    public void onRotateField(KeyEvent event) {
        if (activeModelComboBox.getValue() == null) {
            return;
        }

        String x = xRotateField.getText();
        String y = yRotateField.getText();
        String z = zRotateField.getText();

        if (checkFieldsValues(x, y, z)) {
            return;
        }

        try {
            scene.getActiveModel().setRotate(new Vector3f(Float.parseFloat(x),
                    Float.parseFloat(y),
                    Float.parseFloat(z)));
        } catch (NumberFormatException e) {
            showAlert("Error parsing active model rotate angles.");
        }
    }

    public void onScaleField(KeyEvent event) {
        if (activeModelComboBox.getValue() == null) {
            return;
        }

        String x = xScale.getText();
        String y = yScale.getText();
        String z = zScale.getText();

        if (checkFieldsValues(x, y, z)) {
            return;
        }

        try {
            scene.getActiveModel().setScale(new Vector3f(Float.parseFloat(x),
                    Float.parseFloat(y),
                    Float.parseFloat(z)));
        } catch (NumberFormatException e) {
            showAlert("Error parsing active model scale.");
        }
    }

    public void onTranslateField(KeyEvent inputMethodEvent) {
        if (activeModelComboBox.getValue() == null) {
            return;
        }

        String x = translateX.getText();
        String y = translateY.getText();
        String z = translateZ.getText();

        if (checkFieldsValues(x, y, z)) {
            return;
        }

        try {
            scene.getActiveModel().setTranslatedVector(new Vector3f(Float.parseFloat(x),
                    Float.parseFloat(y),
                    Float.parseFloat(z)));
        } catch (NumberFormatException e) {
            showAlert("Error parsing active model translation.");
        }
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(alertIcon);

        alert.setTitle("Oops");
        alert.setHeaderText("The action cannot be performed.");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onAddCameraButton() {
        try {
            float x = Float.parseFloat(newCameraX.getText());
            float y = Float.parseFloat(newCameraY.getText());
            float z = Float.parseFloat(newCameraZ.getText());

            Camera camera = new Camera(new Vector3f(x, y, z),
                    new Vector3f(0, 0, 0),
                    1.0F, 1, 0.01F, 100);
            scene.addCamera(camera);
            activeCameraComboBox.getItems().add(cameraPosToString(scene.getCameras().size() - 1, camera.getPosition()));
        } catch (NumberFormatException e) {
            showAlert("Error parsing camera coordinates.");
        }
    }
    public void onRemoveVerticeButton(ActionEvent actionEvent) {
        if (activeModelComboBox.getValue() == null) {
            return;
        }
        int x = Integer.parseInt(idVertic.getText());
        Integer[] arrX = new Integer[1];
        arrX[0] = x;
        try {
            AffineModelTransformer activeModel = scene.getActiveModel();
            if (activeModel != null) {
                RemoveVertices.removeVertices(activeModel.getModel(), arrX);
                renderMesh(activeModel);
            } else {
                System.out.println("Active model is null.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error removing vertices: " + e.getMessage());
        }
    }
    public void onRemovePolygonButton(ActionEvent actionEvent) {
        if (activeModelComboBox.getValue() == null) {
            return;
        }
        int x = Integer.parseInt(idPolygon.getText());
        ArrayList<Integer> list_x = new ArrayList<Integer>();
        list_x.add(x);

        scene.getActiveModel().setModel(RemovePolygons.removePolygons(scene.getActiveModel().getTriangulatedModel(), list_x, true));
    }

    private void fillCameras() {
        List<Camera> cameras = scene.getCameras();

        Vector3f curCameraPos;
        for (int i = 0; i < cameras.size(); i++) {
            curCameraPos = cameras.get(i).getPosition();
            activeCameraComboBox.getItems().add(cameraPosToString(i, curCameraPos));
        }

        activeCameraComboBox.setValue(cameraPosToString(0, cameras.get(0).getPosition()));
    }

    private void fillComboBoxModels(ComboBox<String> box) {
        Set<AffineModelTransformer> models = scene.getModels();

        for (var model : models) {
            box.getItems().add(model.getModelName());
        }
    }

    private void fillModels() {
        fillComboBoxModels(modelComboBox);
    }

    private void fillActiveModel() {
        fillComboBoxModels(activeModelComboBox);
    }

    private String cameraPosToString(int index, Vector3f pos) {
        return index + 1 + ". x=" + pos.x + " y=" +
                pos.y + " z=" + pos.z;
    }

    public void setActiveCamera() {
        String curCamera = activeCameraComboBox.getValue();
        int index = Integer.parseInt(curCamera.split("\\.")[0]);

        scene.setActiveCamera(index - 1);
    }

    public void setTextureName() {
        String modelName = modelComboBox.getValue();

        ModelSceneOptions options = scene.getModelOptions(modelName);
        if (modelName == null || options.texture == null) {
            textureLabel.setText("-");
        } else {
            textureLabel.setText(options.textureFileName);
        }
    }

    public void onAddTextureButton() {
        if (modelComboBox.getValue() == null) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Image (*.jpg, *.png)", "*.png", "*.jpg"));
        fileChooser.setTitle("Load Texture");

        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        Image image = new Image(fileName.toUri().toString());

        ModelSceneOptions options = scene.getModelOptions(modelComboBox.getValue());
        options.textureFileName = String.valueOf(fileName.getFileName());
        options.texture = image;

        setTextureName();
        unlockCheckBoxes();
    }

    public void onDeleteTextureButton() {
        if (modelComboBox.getValue() == null || textureLabel.getText().equals("-")) {
            return;
        }

        ModelSceneOptions options = scene.getModelOptions(modelComboBox.getValue());
        options.texture = null;
        options.textureFileName = "";

        checkTextureBox.setIndeterminate(true);
        setTextureName();
    }

    public void setCheckBoxesIndeterminate() {
        checkTextureBox.setIndeterminate(true);
        checkPolygonGridBox.setIndeterminate(true);
        checkUseLightBox.setIndeterminate(true);
    }

    public void unlockCheckBoxes() {
        if (modelComboBox.getValue() != null) {
            ModelSceneOptions options = scene.getModelOptions(modelComboBox.getValue());

            checkPolygonGridBox.setIndeterminate(false);
            checkPolygonGridBox.setSelected(options.usedPolygonalGrid);

            checkUseLightBox.setIndeterminate(false);
            checkUseLightBox.setSelected(options.usedLighting);

            if (options.texture != null) {
                checkTextureBox.setIndeterminate(false);
                checkTextureBox.setSelected(options.usedTexture);
            } else {
                checkTextureBox.setIndeterminate(true);
            }
            setTextureName();
        } else {
            setCheckBoxesIndeterminate();
        }
    }

    public void updateModelSceneOptions() {
        String modelName = modelComboBox.getValue();

        if (modelName == null) {
            setCheckBoxesIndeterminate();
            return;
        }

        if (textureLabel.getText().equals("-")) {
            checkTextureBox.setIndeterminate(true);
        }
        updateOptions(modelName);
        renderMesh(scene.getModel(modelName));
    }

    private void updateOptions(String modelName) {
        ModelSceneOptions options = scene.getModelOptions(modelName);
        options.usedLighting = checkUseLightBox.isSelected();
        options.usedTexture = !checkTextureBox.isIndeterminate() && checkTextureBox.isSelected();
        options.usedPolygonalGrid = checkPolygonGridBox.isSelected();
    }

    @FXML
    private void setActiveModel() {
        String modelName = activeModelComboBox.getValue();

        if (modelName == null) {
            return;
        }

        scene.setActiveModel(modelName);
    }

    public void onSaveModelButton(ActionEvent actionEvent) {
        if (activeModelComboBox.getValue() == null) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model");

        File file = fileChooser.showSaveDialog(canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());
        try {
            PrintWriter out = new PrintWriter(new File(fileName.toUri()));
            out.println(ObjWriter.getContent(scene.getModel(activeModelComboBox.getValue()).getTriangulatedModel()));
            out.close();
        } catch (FileNotFoundException e) {
            showAlert("File with this name not found");
        }
    }
}
