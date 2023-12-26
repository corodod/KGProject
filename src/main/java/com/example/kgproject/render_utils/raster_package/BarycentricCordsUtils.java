package com.example.kgproject.render_utils.raster_package;

import com.example.kgproject.math.MathMethods;
import com.example.kgproject.math.Vector2f;
import com.example.kgproject.math.Vector3f;

public class BarycentricCordsUtils {
    public static float getZ(
            int currentX, int currentY, PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint
    ) {
        float beta = calculateBeta(currentX, currentY, firstPoint, secondPoint, thirdPoint);
        float alpha = calculateAlpha(currentX, currentY, firstPoint, secondPoint, thirdPoint, beta);



        if (Float.isNaN(alpha) || Float.isNaN(beta)) {
            System.out.println(currentX + " " + currentY);
            System.out.println(alpha + " " + beta + " это getZ");
        }

        return alpha * firstPoint.getZ() + beta * secondPoint.getZ() + (1 - alpha - beta) * thirdPoint.getZ();
    }

    public static Vector3f getNormal(
            int currentX, int currentY, PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint
    ) {
        Vector3f firstNormal = firstPoint.getNormal();
        Vector3f secondNormal = secondPoint.getNormal();
        Vector3f thirdNormal = thirdPoint.getNormal();

        return getVector3f(currentX, currentY, firstPoint, secondPoint, thirdPoint,
                firstNormal, secondNormal, thirdNormal);
    }

    public static Vector3f getWorldCoordinates(
            int currentX, int currentY, PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint
    ) {
        Vector3f firstWorldCoordinates = firstPoint.getWorldCoordinates();
        Vector3f secondWorldCoordinates = secondPoint.getWorldCoordinates();
        Vector3f thirdWorldCoordinates = thirdPoint.getWorldCoordinates();

        return getVector3f(currentX, currentY, firstPoint, secondPoint, thirdPoint,
                firstWorldCoordinates, secondWorldCoordinates, thirdWorldCoordinates);
    }

    private static Vector3f getVector3f(
            int currentX, int currentY, PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint,
            Vector3f firstVector, Vector3f secondVector, Vector3f thirdVector
    ) {
        float beta = calculateBeta(currentX, currentY, firstPoint, secondPoint, thirdPoint);
        float alpha = calculateAlpha(currentX, currentY, firstPoint, secondPoint, thirdPoint, beta);

        if (Float.isNaN(alpha) || Float.isNaN(beta)) {
            System.out.println(alpha + " " + beta + " это getVector3f");
        }

        float x =  alpha * firstVector.x + beta * secondVector.x + (1 - alpha - beta) * thirdVector.x;
        float y =  alpha * firstVector.y + beta * secondVector.y + (1 - alpha - beta) * thirdVector.y;
        float z = alpha * firstVector.z + beta * secondVector.z + (1 - alpha - beta) * thirdVector.z;

        return new Vector3f(x, y, z);
    }

    public static Vector2f getTexture(
            int currentX, int currentY, PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint
    ) {
        float beta = calculateBeta(currentX, currentY, firstPoint, secondPoint, thirdPoint);
        float alpha = calculateAlpha(currentX, currentY, firstPoint, secondPoint, thirdPoint, beta);


        if (Float.isNaN(alpha) || Float.isNaN(beta)) {
            System.out.println(alpha + " " + beta + " это getTexture");
        }

        Vector2f firstTexture = firstPoint.getTexture();
        Vector2f secondTexture = secondPoint.getTexture();
        Vector2f thirdTexture = thirdPoint.getTexture();

        float x =  alpha * firstTexture.x + beta * secondTexture.x + (1 - alpha - beta) * thirdTexture.x;
        float y =  alpha * firstTexture.y + beta * secondTexture.y + (1 - alpha - beta) * thirdTexture.y;

        return new Vector2f(x, y);
    }

    private static float calculateBeta(
            int currentX, int currentY, PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint
    ) {
        float numerator = firstPoint.getX() * (currentY - thirdPoint.getY()) +
                currentX * (thirdPoint.getY() - firstPoint.getY()) + thirdPoint.getX() * (firstPoint.getY() - currentY);

        if ((firstPoint.getX() == 0 || secondPoint.getY() == thirdPoint.getY()) &&
                (secondPoint.getX() == 0 || thirdPoint.getY() == firstPoint.getY()) &&
                (thirdPoint.getX() == 0 || firstPoint.getY() == secondPoint.getY())) {

            return 0;
        } else {
            if (MathMethods.isEqual(numerator, 0))
                return  0;

            float denominator = firstPoint.getX() * (secondPoint.getY() - thirdPoint.getY()) +
                    secondPoint.getX() * (thirdPoint.getY() - firstPoint.getY()) +
                    thirdPoint.getX() * (firstPoint.getY() - secondPoint.getY());

            float beta = numerator / denominator;

            if (Float.isInfinite(beta)) {
                return  0;
            }

            return beta;
        }
    }

    private static float calculateAlpha(
            int currentX, int currentY, PolygonVertex firstPoint, PolygonVertex secondPoint, PolygonVertex thirdPoint,
            float beta
    ) {
        if (firstPoint.getX() == thirdPoint.getX() &&
                firstPoint.getY() == thirdPoint.getY()) {
            return  0;
        } else {

            if (firstPoint.getX() == thirdPoint.getX()) {
                return (currentY - thirdPoint.getY() + beta * (thirdPoint.getY() - secondPoint.getY())) /
                        (firstPoint.getY() - thirdPoint.getY());
            } else {
                return (currentX - thirdPoint.getX() - beta * (secondPoint.getX() - thirdPoint.getX())) /
                        (firstPoint.getX() - thirdPoint.getX());
            }
        }
    }
}

