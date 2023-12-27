package com.cgvsu.kgproject.objreader;


import com.cgvsu.kgproject.math.Vector2f;
import com.cgvsu.kgproject.math.Vector3f;
import com.cgvsu.kgproject.model.Model;
import com.cgvsu.kgproject.model.Polygon;
import com.cgvsu.kgproject.objtoken.ObjToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ObjReader {

    public static Model read(String fileContent) {
        Scanner scanner = new Scanner(fileContent);

        Model model = new Model();

        int fileLineId = 0;
        while (scanner.hasNextLine()) {
            fileLineId++;

            String line = scanner.nextLine();
            List<String> wordsInLine = new ArrayList<>(List.of(line.split("\\s+")));
            if (wordsInLine.isEmpty() || wordsInLine.get(0).equals("")) {
                continue;
            }

            ObjToken token = ObjToken.fromString(wordsInLine.get(0));
            wordsInLine.remove(0);

            switch (token) {
                case DEFAULT -> {}
                case VERTEX -> model.vertices.add(parseVertex(wordsInLine, fileLineId));
                case TEXTURE -> model.textureVertices.add(parseTextureVertex(wordsInLine, fileLineId));
                case NORMAL -> model.normals.add(parseNormal(wordsInLine, fileLineId));
                case FACE -> model.polygons.add(parseFace(wordsInLine, fileLineId));
                case COMMENT -> model.comments.put(fileLineId, listToString(wordsInLine));
                case MATERIAL -> model.materials.put(fileLineId, parseMaterial(model, wordsInLine, fileLineId));
                case MATERIAL_LIB -> model.mtlFileName = parseMaterialLib(wordsInLine, fileLineId);
            }
        }

        return model;
    }

    public static String parseMaterialLib(List<String> wordsInLine, int fileLineId) {
        if (wordsInLine.size() != 1) {
            throw new ObjReaderException("The .mtl file must be one.", fileLineId);
        }

        String[] words = wordsInLine.get(0).split("\\.");
        if (words.length == 1 || !words[words.length - 1].equals("mtl")) {
            throw new ObjReaderException("The mtllib command must contain a file with the .mtl extension.",
                    fileLineId);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            sb.append(words[i]).append(".");
        }

        return sb.append("mtl").toString();
    }

    public static String parseMaterial(Model model, List<String> wordsInLine, int fileLineId) {
        if (model.mtlFileName.equals("")) {
            throw new ObjReaderException("The .mtl file for taking textures is not specified.", fileLineId);
        }

        if (wordsInLine.size() != 1) {
            throw new ObjReaderException("The title of the material must be 1 word.", fileLineId);
        }

        return wordsInLine.get(0);
    }

    private static String listToString(List<String> words) {
        if (words.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(words.get(0));

        for (int i = 1; i < words.size(); i++) {
            sb.append(" ").append(words.get(i));
        }

        return sb.toString();
    }

    public static Vector3f parseVertex(List<String> wordsInLineWithoutToken, int fileLineId) {
        if (wordsInLineWithoutToken.size() != 3) {
            throw new ObjReaderException("Error reading a vertex. Only x, y, z coordinates " +
                    "should be present in the description.", fileLineId);
        }

        try {
            return new Vector3f(
                    Float.parseFloat(wordsInLineWithoutToken.get(0)),
                    Float.parseFloat(wordsInLineWithoutToken.get(1)),
                    Float.parseFloat(wordsInLineWithoutToken.get(2)));
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", fileLineId);
        }
    }

    public static Vector2f parseTextureVertex(List<String> wordsInLineWithoutToken, int fileLineId) {
        if (wordsInLineWithoutToken.size() < 2 || wordsInLineWithoutToken.size() > 3) {
            throw new ObjReaderException("Error reading texture vertex. Only u, v arguments should be present " +
                    "in the description.", fileLineId);
        }

        try {
            return new Vector2f(
                    Float.parseFloat(wordsInLineWithoutToken.get(0)),
                    Float.parseFloat(wordsInLineWithoutToken.get(1)));
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", fileLineId);

        }
    }

    public static Vector3f parseNormal(List<String> wordsInLineWithoutToken, int fileLineId) {
        if (wordsInLineWithoutToken.size() != 3) {
            throw new ObjReaderException("Error reading a normal. Only x, y, z coordinates " +
                    "should be present in the description.", fileLineId);
        }

        try {
            return new Vector3f(
                    Float.parseFloat(wordsInLineWithoutToken.get(0)),
                    Float.parseFloat(wordsInLineWithoutToken.get(1)),
                    Float.parseFloat(wordsInLineWithoutToken.get(2)));
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", fileLineId);
        }
    }


    public static Polygon parseFace(List<String> wordsInLineWithoutToken, int fileLineId) {
        List<Integer> polygonVertexIndices = new ArrayList<>();
        List<Integer> polygonTextureVertexIndices = new ArrayList<>();
        List<Integer> polygonNormalIndices = new ArrayList<>();

        int descriptionType = 0;
        for (String value : wordsInLineWithoutToken) {
            descriptionType = parseFaceWord(descriptionType, value, polygonVertexIndices, polygonTextureVertexIndices, polygonNormalIndices,
                    fileLineId);
        }

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(polygonVertexIndices);
        polygon.setTextureVertexIndices(polygonTextureVertexIndices);
        polygon.setNormalIndices(polygonNormalIndices);

        return polygon;
    }

    public static int parseFaceWord(int descriptionType,
                                    String wordInLine,
                                    List<Integer> polygonVertexIndices,
                                    List<Integer> polygonTextureVertexIndices,
                                    List<Integer> polygonNormalIndices,
                                    int fileLineId) {
        try {
            String[] wordIndices = wordInLine.split("/");
            switch (wordIndices.length) {
                case 1 -> {
                    if (descriptionType == 0) {
                        descriptionType = 1;
                    }

                    if (descriptionType == 1) {
                        polygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);
                    } else {
                        causeErrPolygonVertexDescription(fileLineId);
                    }
                }
                case 2 -> {
                    if (descriptionType == 0) {
                        descriptionType = 2;
                    }

                    if (descriptionType == 2) {
                        polygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);
                        polygonTextureVertexIndices.add(Integer.parseInt(wordIndices[1]) - 1);
                    } else {
                        causeErrPolygonVertexDescription(fileLineId);
                    }
                }
                case 3 -> {
                    if (descriptionType == 0) {
                        descriptionType = wordIndices[1].equals("") ? 3 : 4;
                    }

                    if (descriptionType == 3) {
                        if (wordIndices[1].equals("")) {
                            polygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);
                            polygonNormalIndices.add(Integer.parseInt(wordIndices[2]) - 1);
                        } else {
                            causeErrPolygonVertexDescription(fileLineId);
                        }
                    } else if (descriptionType == 4) {
                        polygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);
                        polygonTextureVertexIndices.add(Integer.parseInt(wordIndices[1]) - 1);
                        polygonNormalIndices.add(Integer.parseInt(wordIndices[2]) - 1);
                    } else {
                        causeErrPolygonVertexDescription(fileLineId);
                    }
                }
                default -> throw new ObjReaderException("Invalid polygon vertex description.", fileLineId);
            }

        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse int value.", fileLineId);
        } catch (IndexOutOfBoundsException e) {
            causeErrPolygonVertexDescription(fileLineId);
        }

        return descriptionType;
    }

    private static void causeErrPolygonVertexDescription(int fileLineId) {
        throw new ObjReaderException("Polygon vertex descriptions must be in the same format.",
                fileLineId);
    }
}

