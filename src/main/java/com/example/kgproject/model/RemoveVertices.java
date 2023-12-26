package com.example.kgproject.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RemoveVertices {
    public static void removeVertices(Model model, Integer[] vertices) throws IndexOutOfBoundsException {
        deleteVertices(model, Arrays.asList(vertices));
    }

    public static void deleteVertices(Model model, List<Integer> vertices) throws IndexOutOfBoundsException {
        Collections.sort(vertices);
        int offset = 0;

        for (Integer index : vertices) {
            if (index - offset < 0) {
                throw new IndexOutOfBoundsException("Index must be greater than or equal to 0");
            }
            if (index - offset >= model.vertices.size()) {
                throw new IndexOutOfBoundsException("Index is greater than or equal to the number of vertices");
            }

            deleteVertex(model, index - offset);
            indexOffset(model, index - offset);
            offset++;
        }
    }

    private static void deleteVertex(Model model, int vertexIndex) {
        deletePolygons(model, vertexIndex);
        model.vertices.remove(vertexIndex);
    }

    private static void deletePolygons(Model model, int vertexIndex) {
        List<Integer> deleteList = new ArrayList<>();
        int c = 0;
        for (Polygon polygon : model.polygons) {
            for (Integer i : polygon.getVertexIndices()) {
                if (i == vertexIndex) {
                    deleteList.add(c);
                    break;
                }
            }
            c++;
        }

        Collections.sort(deleteList);
        int offSet = 0;
        for (Integer d : deleteList) {
            model.polygons.remove(d - offSet);
            offSet++;
        }
    }

    private static void indexOffset(Model model, int index) {
        for (Polygon polygon : model.polygons) {
            polygon.offset(index);
        }
    }
}

