package com.cgvsu.kgproject.model;


import com.cgvsu.kgproject.math.Vector2f;
import com.cgvsu.kgproject.math.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
    public String modelName = "";

    public List<Vector3f> vertices = new ArrayList<>();
    public List<Vector2f> textureVertices = new ArrayList<>();
    public List<Vector3f> normals = new ArrayList<>();
    public List<Polygon> polygons = new ArrayList<>();

    public Map<Integer, String> comments = new HashMap<>();
    public Map<Integer, String> materials = new HashMap<>();

    public String mtlFileName = "";
}

