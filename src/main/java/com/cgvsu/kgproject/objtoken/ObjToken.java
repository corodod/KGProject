package com.cgvsu.kgproject.objtoken;


public enum ObjToken {
    VERTEX("v"),
    TEXTURE("vt"),
    NORMAL("vn"),
    FACE("f"),
    COMMENT("#"),
    MATERIAL("usemtl"),
    MATERIAL_LIB("mtllib"),
    DEFAULT("default");

    private final String text;

    ObjToken(String text) {
        this.text = text;
    }

    public String toString() {
        return this.text;
    }

    public static ObjToken fromString(String strToken) {
        for (ObjToken token : ObjToken.values()) {
            if (strToken.equals(token.text)) {
                return token;
            }
        }

        return DEFAULT;
    }
}

