package com.cgvsu.kgproject.render_utils.raster_package;

import com.cgvsu.kgproject.math.Vector3f;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

public class PolygonRasterization {

    public static void drawPolygon(
            GraphicsContext graphicsContext, List<PolygonVertex> points, Color color, float[][] zBuffer,
            Vector3f position, Image picture, boolean usedTexture, boolean usedLighting
    ) {
        sortPoints(points);

        PolygonVertex firstPoint = points.get(0);
        PolygonVertex secondPoint = points.get(1);
        PolygonVertex thirdPoint = points.get(2);


        float xAxisIncrement12 = getXAxisIncrement(firstPoint, secondPoint);
        float xAxisIncrement13 = getXAxisIncrement(firstPoint, thirdPoint);

        int[] firstLeftBoard, firstRightBoard, secondLeftBoard, secondRightBoard;

        if (firstPoint.getY() != secondPoint.getY()) {
            PolygonVertex auxiliaryPoint = getAuxiliaryPoint(firstPoint, secondPoint, thirdPoint);

            if (xAxisIncrement13 > xAxisIncrement12) {
                firstLeftBoard = getBresenhamLine(firstPoint, secondPoint, true);
                firstRightBoard = getBresenhamLine(firstPoint, auxiliaryPoint, false);
                secondRightBoard = getBresenhamLine(auxiliaryPoint, thirdPoint, false);
                secondLeftBoard = getBresenhamLine(secondPoint, thirdPoint, true);
            } else {
                firstLeftBoard = getBresenhamLine(firstPoint, auxiliaryPoint, true);
                secondLeftBoard = getBresenhamLine(auxiliaryPoint, thirdPoint, true);
                firstRightBoard = getBresenhamLine(firstPoint, secondPoint, false);
                secondRightBoard = getBresenhamLine(secondPoint, thirdPoint, false);
            }

            DrawingPartsPolygons.drawPartTriangle(graphicsContext, firstPoint.getY(), firstLeftBoard, firstRightBoard,
                    firstPoint, secondPoint, thirdPoint, color, zBuffer, position, picture, usedTexture, usedLighting);
        } else {
            secondLeftBoard = getBresenhamLine(firstPoint, thirdPoint, true);
            secondRightBoard = getBresenhamLine(secondPoint, thirdPoint, false);
        }

        DrawingPartsPolygons.drawPartTriangle(graphicsContext, secondPoint.getY(), secondLeftBoard, secondRightBoard,
                firstPoint, secondPoint, thirdPoint, color, zBuffer, position, picture, usedTexture, usedLighting);
    }

    protected static void sortPoints(List<PolygonVertex> points) {
        points.sort((a, b) -> {
            if (a.getY() == b.getY()) {
                return a.getX() - b.getX();
            } else {
                return a.getY() - b.getY();
            }
        });
    }

    private static PolygonVertex getAuxiliaryPoint(PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint) {
        int dy12 = secondPoint.getY() - firstPoint.getY();
        int dy13 = thirdPoint.getY() - firstPoint.getY();
        int dx13 = thirdPoint.getX() - firstPoint.getX();
        int x = dy12 * dx13 / dy13 + firstPoint.getX();

        return new PolygonVertex(x, secondPoint.getY(), 0, null, null, null);
    }

    private static float getXAxisIncrement(PolygonVertex firstPoint, PolygonVertex secondPoint) {
        if (firstPoint.getY() == secondPoint.getY()) {
            return 0;
        } else {
            float increment = secondPoint.getX() - firstPoint.getX();
            increment /= secondPoint.getY() - firstPoint.getY();
            return increment;
        }
    }

    private static int[] getBresenhamLine(PolygonVertex firstPoint, PolygonVertex secondPoint, boolean isLeftBoard) {
        int x, y, deltaX, deltaY, additionX, additionY, incrementX, incrementY, error, errorIncrease, errorDecrease;
        int initialHeight = firstPoint.getY();

        deltaX = secondPoint.getX() - firstPoint.getX();
        deltaY = secondPoint.getY() - initialHeight;

        int[] array = new int[0];

        try {

            array = new int[deltaY + 1];
        }
        catch (OutOfMemoryError e) {
            System.out.println("outOfMemory " + deltaY);
        }

        Arrays.fill(array, -1);

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
        array[0] = x;

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

            int index = y - initialHeight;

            if (array[index] != -1) {
                if (array[index] > x && isLeftBoard) { // для левой
                    array[index] = x;
                }
            } else {
                array[index] = x;
            }
        }

        return array;
    }


    private static boolean isPoint(PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint) {
        return firstPoint.isScreenCoordinatesEquals(secondPoint) &&
                secondPoint.isScreenCoordinatesEquals(thirdPoint) &&
                thirdPoint.isScreenCoordinatesEquals(firstPoint);
    }



    private static PolygonVertex getPointWithMinimalZ(
            PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint
    ) {
        if (firstPoint.getZ() <= secondPoint.getZ() && firstPoint.getZ() <= thirdPoint.getZ()) {
            return firstPoint;
        }

        if (secondPoint.getZ() <= firstPoint.getZ() && secondPoint.getZ() <= thirdPoint.getZ()) {
            return secondPoint;
        }

        return thirdPoint;
    }

    public static boolean isLine(PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint) {
        int x, y, deltaX, deltaY, additionX, additionY, incrementX, incrementY, error, errorIncrease, errorDecrease;
        int initialHeight = firstPoint.getY();

        deltaX = thirdPoint.getX() - firstPoint.getX();
        deltaY = thirdPoint.getY() - initialHeight;

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

            if (x == secondPoint.getX() && y == thirdPoint.getY()) {
                return true;
            }
        }

        return false;
    }
}