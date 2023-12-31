package com.cgvsu.kgproject.render_utils;

import com.cgvsu.kgproject.math.Matrix4f;
import com.cgvsu.kgproject.math.Vector2f;
import com.cgvsu.kgproject.math.Vector3f;
import com.cgvsu.kgproject.model.AffineModelTransformer;
import com.cgvsu.kgproject.model.Polygon;
import com.cgvsu.kgproject.render_utils.raster_package.DrawingPartsPolygons;
import com.cgvsu.kgproject.render_utils.raster_package.PolygonRasterization;
import com.cgvsu.kgproject.render_utils.raster_package.PolygonVertex;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static com.cgvsu.kgproject.math.Point2f.projectedPointToScreenCoordinates;

public class RenderEngine {
    // ChangedModel, углы, масштабирование, перемещение, Model.
    private static final Color DEFAULT_COLOR = Color.GREY;

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final AffineModelTransformer mesh,
            final int width,
            final int height,
            float[][] zBuffer,
            final Image picture,
            boolean usedPolygonalGrid,
            boolean usedTexture,
            boolean usedLighting) {
        final var viewMatrix = camera.getViewMatrix();
        final var projectionMatrix = camera.getProjectionMatrix();

        final var modelViewProjectionMatrix = new Matrix4f(projectionMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(Matrix4f.identityMatrix());

        final int nPolygons = mesh.getPolygons().size();

        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            Polygon currentPolygon = mesh
                    .getPolygons()
                    .get(polygonInd);

            final int nVerticesInPolygon = currentPolygon
                    .getVertexIndices()
                    .size();

            List<PolygonVertex> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                int currentVertexIndex = currentPolygon
                        .getVertexIndices()
                        .get(vertexInPolygonInd);

                Vector2f texture = null;

                if (usedTexture) {
                    if (mesh.getTexture().size() == 0) {
                        throw new IllegalArgumentException("В модели не указаны текстурные координаты");
                    }

                    int textureIndex = currentPolygon.getTextureVertexIndices().get(vertexInPolygonInd);
                    texture = mesh.getTexture().get(textureIndex);
                }

                Vector3f normal = null;

                if (usedLighting) {
                    normal = mesh.getNormals().get(currentVertexIndex);
                    normal = mesh.getRotationMatrix().multiplyByVector3(normal);
                }

                final var vertex = mesh.getTransformedVector(currentVertexIndex);
                Vector3f rotatedPoint = modelViewProjectionMatrix.multiplyByVector3(vertex);

                final var resultPoint = projectedPointToScreenCoordinates(rotatedPoint, width, height);
                resultPoints.add(new PolygonVertex((int) resultPoint.x, (int) resultPoint.y, rotatedPoint.z,
                        texture, normal, vertex));
                // Здесь мне нужно приведение к int, т.к. растеризация была написана для целых x и y, чтобы не было
                // никаких мили выходов за края полигонов, которые могут возникнуть из-за float
            }

            PolygonRasterization.drawPolygon(graphicsContext, resultPoints, DEFAULT_COLOR, zBuffer,
                    camera.getPosition(), picture, usedTexture, usedLighting);

            //это такая же отрисовка только через DDA, но лучше использовать брезенхема. Если всё-таки захотите
            //проверить с ней, то раскомментируйте строки 80 81 и закомментируйте 75 76
//            PolygonRasterization2.drawPolygon(graphicsContext, resultPoints, DEFAULT_COLOR, zBuffer,
//                    camera.getPosition(), picture, usedTexture, usedLighting);

        }

        if (usedPolygonalGrid) {
            drawPolygonalGrid(mesh, modelViewProjectionMatrix, width, height, graphicsContext, zBuffer);
        }
    }

    private static void drawPolygonalGrid(
            AffineModelTransformer mesh,
            Matrix4f modelViewProjectionMatrix,
            int width,
            int height,
            GraphicsContext graphicsContext,
            float[][] zBuffer) {

        var polygons = mesh.getPolygons();

        for (Polygon currentPolygon : polygons) {
            final int nVerticesInPolygon = currentPolygon
                    .getVertexIndices()
                    .size();


            List<PolygonVertex> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                int currentVertexIndex = currentPolygon
                        .getVertexIndices()
                        .get(vertexInPolygonInd);

                int textureIndex = currentPolygon.getTextureVertexIndices().get(vertexInPolygonInd);
                Vector3f normal = mesh.getNormals().get(currentVertexIndex);

                final var vertex = mesh.getTransformedVector(currentVertexIndex);
                Vector3f rotatedPoint = modelViewProjectionMatrix.multiplyByVector3(vertex);

                final var resultPoint = projectedPointToScreenCoordinates(rotatedPoint, width, height);
                resultPoints.add(new PolygonVertex((int) resultPoint.x, (int) resultPoint.y, rotatedPoint.z,
                        mesh.getTexture().get(textureIndex), normal, vertex));
            }

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                DrawingPartsPolygons.drawLine(graphicsContext, resultPoints.get(vertexInPolygonInd - 1),
                        resultPoints.get(vertexInPolygonInd), Color.BLACK, zBuffer);
            }

            if (nVerticesInPolygon > 0) {
                DrawingPartsPolygons.drawLine(graphicsContext, resultPoints.get(nVerticesInPolygon - 1),
                        resultPoints.get(0), Color.BLACK, zBuffer);
            }
        }
    }
}
