package main.objreader;

import java.util.*;
import main.math.Vector2f;
import main.math.Vector3f;
import main.model.Polygon;
import main.model.Model;

public class ObjReader {

    private static final String OBJ_VERTEX_TOKEN = "v";
    private static final String OBJ_TEXTURE_TOKEN = "vt";
    private static final String OBJ_NORMAL_TOKEN = "vn";
    private static final String OBJ_FACE_TOKEN = "f";
    private static final String OBJ_COMMENT_TOKEN = "#";
    private static List<Integer> polygonIds = new ArrayList<>();

    public static Model read(String fileContent) throws IncorrectFileException {
        ArrayList<Vector3f> vertices = new ArrayList<>();
        ArrayList<Vector2f> textureVertices = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();
        ArrayList<Polygon> polygons = new ArrayList<>();
        int lineInd = 0;
        Scanner scanner = new Scanner(fileContent);
        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            ArrayList<String> wordsInLine = new ArrayList<String>(Arrays.asList(line.trim().split("\\s+")));
            if (wordsInLine.isEmpty()) {
                continue;
            }

            final String token = wordsInLine.get(0);
            wordsInLine.remove(0);

            ++lineInd;
            switch (token) {
                case OBJ_VERTEX_TOKEN -> vertices.add(parseVertex(wordsInLine, lineInd));
                case OBJ_TEXTURE_TOKEN -> textureVertices.add(parseTextureVertex(wordsInLine, lineInd));
                case OBJ_NORMAL_TOKEN -> normals.add(parseNormal(wordsInLine, lineInd));
                case OBJ_FACE_TOKEN -> {
                    polygons.add(parseFace(wordsInLine, lineInd));
                    polygonIds.add(lineInd);
                }
                case OBJ_COMMENT_TOKEN -> System.out.println(line);
                default -> {
                    System.out.println("Warning! Unknown token on line " + lineInd);
                }
            }
        }
        int lengthVertex = vertices.size();
        int lengthTextureVertex = textureVertices.size();
        int lengthNormal = normals.size();
        format(polygons, lengthVertex, lengthTextureVertex, lengthNormal);
        return new Model(vertices, textureVertices, normals, polygons);
    }

    // Всем методам кроме основного я поставил модификатор доступа protected, чтобы обращаться к ним в тестах
    protected static Vector3f parseVertex(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
        if (wordsInLineWithoutToken.size()>3){
            throw new ObjReaderException("Too many vertex arguments.", lineInd);
        }
        try {
            return new Vector3f(
                    Float.parseFloat(wordsInLineWithoutToken.get(0)),
                    Float.parseFloat(wordsInLineWithoutToken.get(1)),
                    Float.parseFloat(wordsInLineWithoutToken.get(2)));
        } catch(NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", lineInd);

        } catch(IndexOutOfBoundsException e) {
            throw new ObjReaderException("Too few vertex arguments.", lineInd);
        }
    }

    protected static Vector2f parseTextureVertex(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
        if (wordsInLineWithoutToken.size()>2){
            throw new ObjReaderException("Too many texture vertex arguments.", lineInd);
        }
        try {
            return new Vector2f(
                    Float.parseFloat(wordsInLineWithoutToken.get(0)),
                    Float.parseFloat(wordsInLineWithoutToken.get(1)));

        } catch(NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", lineInd);

        } catch(IndexOutOfBoundsException e) {
            throw new ObjReaderException("Too few texture vertex arguments.", lineInd);
        }
    }

    protected static Vector3f parseNormal(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
        if (wordsInLineWithoutToken.size()>3){
            throw new ObjReaderException("Too many normal arguments.", lineInd);
        }
        try {
            return new Vector3f(
                    Float.parseFloat(wordsInLineWithoutToken.get(0)),
                    Float.parseFloat(wordsInLineWithoutToken.get(1)),
                    Float.parseFloat(wordsInLineWithoutToken.get(2)));

        } catch(NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", lineInd);

        } catch(IndexOutOfBoundsException e) {
            throw new ObjReaderException("Too few normal arguments.", lineInd);
        }
    }

    protected static Polygon parseFace(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
        if (wordsInLineWithoutToken.size()<3){
            throw new ObjReaderException("Not enough vertex to create polygon.", lineInd);
        }
        ArrayList<Integer> onePolygonVertexIndices = new ArrayList<Integer>();
        ArrayList<Integer> onePolygonTextureVertexIndices = new ArrayList<Integer>();
        ArrayList<Integer> onePolygonNormalIndices = new ArrayList<Integer>();

        parseFaceWord(wordsInLineWithoutToken.get(0), onePolygonVertexIndices, onePolygonTextureVertexIndices, onePolygonNormalIndices, lineInd);
        int baseCountType = onePolygonTextureVertexIndices.size()+onePolygonNormalIndices.size()+onePolygonVertexIndices.size();
        for (int s=1; s<wordsInLineWithoutToken.size(); s++) {
            parseFaceWord(wordsInLineWithoutToken.get(s), onePolygonVertexIndices, onePolygonTextureVertexIndices, onePolygonNormalIndices, lineInd);
            if (baseCountType*(s+1)!=onePolygonTextureVertexIndices.size()+onePolygonNormalIndices.size()+onePolygonVertexIndices.size()){
                throw new ObjReaderException("Incorrect data for polygon.", lineInd);
            }
        }
        Set<Integer> set = new HashSet<>();
        for (Integer vertex: onePolygonVertexIndices) {
            if(!set.add(vertex)){
                throw new ObjReaderException("The vertices of the polygon must not be repeated.", lineInd);
            }
        }
        Polygon result = new Polygon();
        result.setVertexIndices(onePolygonVertexIndices);
        result.setTextureVertexIndices(onePolygonTextureVertexIndices);
        result.setNormalIndices(onePolygonNormalIndices);
        return result;
    }

    protected static void parseFaceWord(
            String wordInLine,
            ArrayList<Integer> onePolygonVertexIndices,
            ArrayList<Integer> onePolygonTextureVertexIndices,
            ArrayList<Integer> onePolygonNormalIndices,
            int lineInd) {
        try {
            String[] wordIndices = wordInLine.split("/");
            switch (wordIndices.length) {
                case 1 -> {
                    onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]));
                }
                case 2 -> {
                    onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]));
                    onePolygonTextureVertexIndices.add(Integer.parseInt(wordIndices[1]));
                }
                case 3 -> {
                    onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]));
                    onePolygonNormalIndices.add(Integer.parseInt(wordIndices[2]));
                    if (!wordIndices[1].equals("")) {
                        onePolygonTextureVertexIndices.add(Integer.parseInt(wordIndices[1]));
                    }
                }
                default -> {
                    throw new ObjReaderException("Invalid element size.", lineInd);
                }
            }

        } catch(NumberFormatException e) {
            throw new ObjReaderException("Failed to parse int value.", lineInd);

        } catch(IndexOutOfBoundsException e) {
            throw new ObjReaderException("Too few arguments.", lineInd);
        }
    }
    protected static void format(ArrayList<Polygon> polygons, int lengthVertex, int lengthTextureVertex, int lengthNormal) {
        int count = 0;
        for (Polygon pol: polygons) {
            ArrayList<Integer> vertexIndices = pol.getVertexIndices();
            ArrayList<Integer> textureVertexIndices = pol.getTextureVertexIndices();
            ArrayList<Integer> normalIndices = pol.getNormalIndices();

            formatArray(vertexIndices, lengthVertex, count);
            formatArray(textureVertexIndices, lengthTextureVertex, count);
            formatArray(normalIndices, lengthNormal, count);

            count++;
        }
    }
    protected static void formatArray(ArrayList<Integer> array, int n, int count) {
        for (int i = 0; i < array.size(); i++) {
            if (Math.abs(array.get(i))>n){
                throw new ObjReaderException("Incorrect polygon parameters set.", polygonIds.get(count));
            }else {
                if (array.get(i) > 0) {
                    array.set(i, array.get(i) - 1);
                } else {
                    array.set(i, array.get(i) + n);
                }
            }
        }
    }
}
