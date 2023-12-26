package com.example.kgproject.render_utils.raster_package;

import com.example.kgproject.math.Vector2f;
import com.example.kgproject.math.Vector3f;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class DrawingPartsPolygons {
    public static void drawLine(
            GraphicsContext graphicsContext, PolygonVertex firstPoint, PolygonVertex secondPoint, Color color,
            float[][] zBuffer
    ) {
        int x, y, deltaX, deltaY, additionX, additionY, incrementX, incrementY, error, errorIncrease, errorDecrease;
        int initialHeight = firstPoint.getY();

        deltaX = secondPoint.getX() - firstPoint.getX();
        deltaY = secondPoint.getY() - initialHeight;

        incrementX = Integer.compare(deltaX, 0);
        incrementY = Integer.compare(deltaY, 0);

        deltaX = Math.abs(deltaX);
        deltaY = Math.abs(deltaY);

        if (deltaX > deltaY) {
            additionX = incrementX;
            additionY = 0;
            errorIncrease = deltaX;
            errorDecrease = deltaY;
        } else {
            additionX = 0;
            additionY = incrementY;
            errorIncrease = deltaY;
            errorDecrease = deltaX;
        }

        error = errorIncrease / 2;
        x = firstPoint.getX();
        y = initialHeight;


        for (int i = 0; i < errorIncrease; i++) {
            error -= errorDecrease;

            if (error < 0) {
                error += errorIncrease;
                x += incrementX;
                y += incrementY;
            } else {
                x += additionX;
                y += additionY;
            }

            if (y >= 0 && y < zBuffer.length && x >= 0 && x < zBuffer[0].length) {
                zBuffer[y][x] = Float.MIN_VALUE;

                graphicsContext.getPixelWriter().setColor(x, y, color);
            }
        }
    }

    public static void drawPartTriangle(
            GraphicsContext graphicsContext, int startY, int[] leftArray, int[] rightArray, PolygonVertex firstPoint,
            PolygonVertex secondPoint, PolygonVertex thirdPoint, Color color, float[][] zBuffer, Vector3f position,
            Image picture, boolean usedTexture, boolean usedLighting
    ) {
        Color preColor = color;
        for (int i = 0; i < leftArray.length; i++) {
            int leftBoard = leftArray[i];
            int rightBoard = rightArray[i];
            int y = i + startY;

            if (y < 0 || y >= zBuffer.length) {
                continue;
            }

            for (int x = leftBoard; x <= rightBoard; x++) {
                if (x < 0 || x >= zBuffer[0].length) {
                    continue;
                }

                float z = BarycentricCordsUtils.getZ(x, y, firstPoint, secondPoint, thirdPoint);
//                if (z < 0 || z > 1) {
//                    continue;
//                }

                if (/*zBuffer[y][x] == 0 || */ zBuffer[y][x] > z) {
                    zBuffer[y][x] = z;
                    color = preColor;

                    if (usedTexture) {
                        color = getTexturePixelColor(firstPoint, secondPoint, thirdPoint, x, y, picture);
                    }

                    if (usedLighting) {
                        color = getIlluminatedColor(firstPoint, secondPoint, thirdPoint, color, position, x, y);
                    }

                    graphicsContext.getPixelWriter().setColor(x, y, color);
                }
            }
        }
    }

    public  static  void drawPartTriangle2(
            GraphicsContext graphicsContext, int startY, int endY, float leftBoard, float rightBoard, float leftIncrement,
            float rightIncrement, PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint,
            Color color, float[][] zBuffer, Vector3f position, Image picture, boolean usedTexture, boolean usedLighting
    ) {
        Color initialColor = color;
        for (int y = startY; y <= endY; y++) {
            if (y < 0 || y >= zBuffer.length) {
                continue;
            }

            for (int x = (int) leftBoard; x <= (int) rightBoard; x++) {
                if (x < 0 || x >= zBuffer[0].length) {
                    continue;
                }

                float z = BarycentricCordsUtils.getZ(x, y, firstPoint, secondPoint, thirdPoint);

                if (zBuffer[y][x] > z) {
                    zBuffer[y][x] = z;
                    color = initialColor;

                    if (usedTexture) {
                        color = getTexturePixelColor(firstPoint, secondPoint, thirdPoint, x, y, picture);
                    }

                    if (usedLighting) {
                        color = getIlluminatedColor(firstPoint, secondPoint, thirdPoint, color, position, x, y);
                    }

                    graphicsContext.getPixelWriter().setColor(x, y, color);
                }            }

            leftBoard += leftIncrement;
            rightBoard += rightIncrement;
        }
    }

    private static Color getIlluminatedColor(
            PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint, Color color, Vector3f position,
            int x, int y
    ) {
        Vector3f worldCoordinates = BarycentricCordsUtils.getWorldCoordinates(x, y, firstPoint, secondPoint, thirdPoint);
        Vector3f cameraVector = Vector3f.fromTwoPoints(worldCoordinates, position);
        cameraVector.normalize();

        Vector3f normal = BarycentricCordsUtils.getNormal(x, y, firstPoint, secondPoint, thirdPoint);
        normal.normalize();


        float ratio = cameraVector.dot(normal);

        if (ratio < 0) {
            ratio = 0;
        }
        ratio /= 2;
        ratio += 0.37F;

        float r = (float) (ratio * color.getRed());
        float g = (float) (ratio * color.getGreen());
        float b = (float) (ratio * color.getBlue());

        return new Color(r, g, b, 1.0);
    }

    private static Color getTexturePixelColor(
            PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint, int x, int y, Image picture
    ) {
        Vector2f currentTexture = BarycentricCordsUtils.getTexture(x, y, firstPoint, secondPoint, thirdPoint);


        int width = (int) picture.getWidth();
        int height = (int) picture.getHeight();

        currentTexture.y = 1 - currentTexture.y;
        currentTexture.x *= width;
        currentTexture.y *= height;

        currentTexture.x = ((int) currentTexture.x >= width) ? width - 1 : currentTexture.x;
        currentTexture.y = ((int) currentTexture.y >= height) ? height - 1 : currentTexture.y;

        currentTexture.x = ((int) currentTexture.x <= 0) ? 0 : currentTexture.x;
        currentTexture.y = ((int) currentTexture.y <= 0) ? 0 : currentTexture.y;


        return picture.getPixelReader().getColor((int) currentTexture.x, (int) currentTexture.y);
    }
}

