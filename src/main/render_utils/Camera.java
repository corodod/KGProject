package main.render_utils;

import main.math.Matrix4f;
import main.math.Vector3f;

public final class Camera {
    private final Vector3f position;
    private final Vector3f target;
    private final float fov;
    private float aspectRatio;
    private final float nearPlane;
    private final float farPlane;

    private float horizontalAngle;

    private float verticalAngle;

    private final float distance;


    public Camera(
            final Vector3f position,
            final Vector3f target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;

        this.horizontalAngle = 0;
        this.verticalAngle = 0;
        this.distance = (float) Math.sqrt((position.x - target.x) * (position.x - target.x)
                +(position.y - target.y) * (position.y - target.y)
                +(position.z - target.z) * (position.z - target.z));
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public void movePosition(MovementVector vector, float translation) {
        var radianHorizontalAngle = Math.toRadians(horizontalAngle);
        switch (vector) {
            case UP -> {
                final var temp = new Vector3f(0, translation, 0);
                this.position.add(temp);
                this.target.add(temp);
            }
            case DOWN -> {
                final var temp = new Vector3f(0, -translation, 0);
                this.position.add(temp);
                this.target.add(temp);
            }
            case BACKWARD -> {
                final var temp = new Vector3f((float) (translation * Math.sin(radianHorizontalAngle)),
                        0,
                        (float) (translation * Math.cos(radianHorizontalAngle)));
                this.position.add(temp);
                this.target.add(temp);
            }
            case FORWARD -> {
                final var temp = new Vector3f(-(translation * (float) Math.sin(radianHorizontalAngle)),
                        0,
                        -(translation * (float) Math.cos(radianHorizontalAngle)));
                this.position.add(temp);
                this.target.add(temp);
            }
            case LEFT -> {
                final var temp = new Vector3f((translation * (float) Math.cos(radianHorizontalAngle)),
                        0,
                        (translation * (float) Math.sin(radianHorizontalAngle)));
                this.position.add(temp);
                this.target.add(temp);
            }
            case RIGHT -> {
                final var temp = new Vector3f((float) -(translation * Math.cos(radianHorizontalAngle)),
                        0,
                        (float) -(translation * Math.sin(radianHorizontalAngle)));
                this.position.add(temp);
                this.target.add(temp);
            }
        }
    }

    public void rotateCameraHorizontal(float translation) {
        horizontalAngle += translation;
        if (horizontalAngle > 360) {
            horizontalAngle -= 360;
        } else if (horizontalAngle < -360) {
            horizontalAngle += 360;
        }
        final var deltaTargetPosition = new Vector3f();
        deltaTargetPosition.sub(target, position);

        float inRadians = (float) Math.toRadians(horizontalAngle);
        final var newTarget = new Vector3f((float) Math.sin(inRadians),
                0,
                (float) (1 - Math.cos(inRadians)));

        this.target.x = newTarget.x * distance;
        this.target.z = newTarget.z * distance;

        if(horizontalAngle == 180) {
            this.target.z = position.z - distance + deltaTargetPosition.z;
            this.target.x = 0;
        }
        if(horizontalAngle == 0) {
            this.target.z = position.z + deltaTargetPosition.z;
            this.target.x = 0;
        }
        if(Math.abs(horizontalAngle) == 90 || Math.abs(horizontalAngle) == 270) {
            this.target.z = 0;
        }
    }

    public void rotateCameraVertical(float translation) {
        verticalAngle += translation;
        if (verticalAngle > 360) {
            verticalAngle -= 360;
        } else if (verticalAngle < -360) {
            verticalAngle += 360;
        }
        final var deltaTargetPosition = new Vector3f();
        deltaTargetPosition.sub(target, position);

        float inRadians = (float) Math.toRadians(verticalAngle);
        final var newTarget = new Vector3f((float) 0,
                (float) Math.sin(inRadians),
                (float) (1 - Math.cos(inRadians)));

        this.target.y = newTarget.y * distance;
        this.target.z = newTarget.z * distance;

        if(verticalAngle == 180) {
            this.target.z = position.z - distance + deltaTargetPosition.z;
            this.target.y = 0;
        }
        if(verticalAngle == 0) {
            this.target.z = position.z + deltaTargetPosition.z;
            this.target.y = 0;
        }
        if(Math.abs(verticalAngle) == 90 || Math.abs(verticalAngle) == 270) {
            this.target.z = 0;
        }
    }

    Matrix4f getViewMatrix() {
        return CoordinateTransformer.lookAt(position, target);
    }

    public Vector3f getPosition() {
        return position;
    }

    Matrix4f getProjectionMatrix() {
        return CoordinateTransformer.perspective(fov, aspectRatio, nearPlane, farPlane);
    }
}
