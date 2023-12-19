package test.objreader;

import org.junit.jupiter.api.Test;
//Junit 5.81

import main.objreader.ObjReader;
import main.objreader.ObjReaderException;
import main.math.Vector2f;
import main.math.Vector3f;
import main.model.Polygon;
import main.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObjReaderTest {

    @Test
    void readWithIncorrectToken() {
        String fileContent = "vr 1 1 1";

        try {
            ObjReader.read(fileContent);
        } catch (ObjReaderException e) {
            String expectedErr = "Error parsing OBJ file on line: 1. Illegal OBJ token name: vr.";
            assertEquals(expectedErr, e.getMessage());
        }
    }

    @Test
    void read() {
        String fileContent = """
                v 1 1 1
                v 2 2 2
                v 3 3 3""";

        List<Vector3f> expectedVertices = Arrays.asList(
                new Vector3f(1, 1, 1),
                new Vector3f(2, 2, 2),
                new Vector3f(3, 3, 3)
        );

        Model actualModel = ObjReader.read(fileContent);

        assertEquals(expectedVertices, actualModel.vertices);
    }

    @Test
    void parseVertexWith2Coordinates() {
        List<String> coordinates = Arrays.asList("12", "1");

        try {
            ObjReader.parseVertex(coordinates, 1);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 1. Error reading a vertex. Only x, y, z coordinates " +
                    "should be present in the description.";
            assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    void parseVertexWith3Letters() {
        List<String> coordinates = Arrays.asList("a", "b", "c");

        try {
            ObjReader.parseVertex(coordinates, 1);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 1. Failed to parse float value.";
            assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    void parseVertexWith3Coordinates() {
        List<String> coordinates = Arrays.asList("1", "2", "3");

        Vector3f actualVector = ObjReader.parseVertex(coordinates, 1);
        Vector3f expectedVector = new Vector3f(1, 2, 3);

        assertEquals(expectedVector, actualVector);
    }

    @Test
    void parseTextureVertexWith0Coordinates() {
        List<String> coordinates = new ArrayList<>();

        try {
            ObjReader.parseTextureVertex(coordinates, 1);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 1. Error reading texture vertex. Only u, v arguments " +
                    "should be present in the description.";
            assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    void parseTextureVertexWith2Letters() {
        List<String> coordinates = Arrays.asList("a", "b");

        try {
            ObjReader.parseTextureVertex(coordinates, 1);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 1. Failed to parse float value.";
            assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    void parseTextureVertexWith2Coordinates() {
        List<String> coordinates = Arrays.asList("1", "2");

        Vector2f actualVector = ObjReader.parseTextureVertex(coordinates, 1);
        Vector2f expectedVector = new Vector2f(1, 2);

        assertEquals(expectedVector, actualVector);
    }

    @Test
    void parseNormalWith2Coordinates() {
        List<String> coordinates = Arrays.asList("12", "1");

        try {
            ObjReader.parseNormal(coordinates, 1);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 1. Error reading a normal. Only x, y, z coordinates " +
                    "should be present in the description.";
            assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    void parseNormalWith3Letters() {
        List<String> coordinates = Arrays.asList("a", "b", "c");

        try {
            ObjReader.parseNormal(coordinates, 1);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 1. Failed to parse float value.";
            assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    void parseFaceWithDifferentVertexDescription() {
        List<String> polygonVertexes = Arrays.asList("1//1", "2/2/2");

        try {
            ObjReader.parseFace(polygonVertexes, 1);
        } catch (ObjReaderException e) {
            String expectedErr = "Error parsing OBJ file on line: 1. Polygon vertex descriptions must be in the " +
                    "same format.";
            assertEquals(expectedErr, e.getMessage());
        }
    }

    @Test
    void parseFace() {
        List<String> polygonVertexes = Arrays.asList("1/1/1", "2/2/2", "3/3/3");

        List<Integer> expectedVertexIndices = Arrays.asList(0, 1, 2);
        List<Integer> expectedTextureVertexIndices = Arrays.asList(0, 1, 2);
        List<Integer> expectedNormalIndices = Arrays.asList(0, 1, 2);

        Polygon actualPolygon = ObjReader.parseFace(polygonVertexes, 1);

        assertEquals(expectedVertexIndices, actualPolygon.getVertexIndices());
        assertEquals(expectedTextureVertexIndices, actualPolygon.getTextureVertexIndices());
        assertEquals(expectedNormalIndices, actualPolygon.getNormalIndices());
    }

    @Test
    void parseFaceWordWith4Indices() {
        List<Integer> polygonVertexIndices = new ArrayList<>();
        List<Integer> polygonTextureVertexIndices = new ArrayList<>();
        List<Integer> polygonNormalIndices = new ArrayList<>();

        String vertex = "1/1/1/1";

        try {
            ObjReader.parseFaceWord(0, vertex, polygonVertexIndices, polygonTextureVertexIndices, polygonNormalIndices, 1);
        } catch (ObjReaderException e) {
            String expectedErr = "Error parsing OBJ file on line: 1. Invalid polygon vertex description.";
            assertEquals(expectedErr, e.getMessage());
        }
    }

    @Test
    void parseFaceWordWith3Letters() {
        List<Integer> polygonVertexIndices = new ArrayList<>();
        List<Integer> polygonTextureVertexIndices = new ArrayList<>();
        List<Integer> polygonNormalIndices = new ArrayList<>();

        String vertex = "a/b/c";

        try {
            assertEquals(4, ObjReader.parseFaceWord(0, vertex, polygonVertexIndices, polygonTextureVertexIndices, polygonNormalIndices, 1));
        } catch (ObjReaderException e) {
            String expectedErr = "Error parsing OBJ file on line: 1. Failed to parse int value.";
            assertEquals(expectedErr, e.getMessage());
        }
    }

    @Test
    void parseFaceWordWith2IndicesWithoutTextureVertexIndex() {
        List<Integer> polygonVertexIndices = new ArrayList<>();
        List<Integer> polygonTextureVertexIndices = new ArrayList<>();
        List<Integer> polygonNormalIndices = new ArrayList<>();

        String vertex = "1//1";

        assertEquals(3, ObjReader.parseFaceWord(0, vertex, polygonVertexIndices, polygonTextureVertexIndices, polygonNormalIndices, 1));
        assertEquals(0, polygonVertexIndices.get(0));
        assertEquals(0, polygonNormalIndices.get(0));
        assertEquals(0, polygonTextureVertexIndices.size());
    }

    @Test
    void parseMaterialLibWithMultipleFiles() {
        List<String> wordsInLine = Arrays.asList("ogjd.mtl", "dfjs.mtl");

        try {
            ObjReader.parseMaterialLib(wordsInLine, 1);
        } catch (ObjReaderException e) {
            String expectedErr = "Error parsing OBJ file on line: 1. The .mtl file must be one.";
            assertEquals(expectedErr, e.getMessage());
        }
    }

    @Test
    void parseMaterialLibWithoutMtlPermission() {
        List<String> wordsInLine = List.of("ogjd.txt");

        try {
            ObjReader.parseMaterialLib(wordsInLine, 1);
        } catch (ObjReaderException e) {
            String expectedErr = "Error parsing OBJ file on line: 1. The mtllib command must contain a file with " +
                    "the .mtl extension.";
            assertEquals(expectedErr, e.getMessage());
        }
    }

    @Test
    void parseMaterialLibWithMtlPermission() {
        List<String> wordsInLine = List.of("ogjd.txt.mtl");

        String expectedFileName = "ogjd.txt.mtl";
        String actualFileName = ObjReader.parseMaterialLib(wordsInLine, 1);

        assertEquals(expectedFileName, actualFileName);
    }

    @Test
    void parseMaterialWithoutMtllib() {
        Model model = new Model();

        List<String> wordsInLine = List.of("tree");

        try {
            ObjReader.parseMaterial(model, wordsInLine, 1);
        } catch (ObjReaderException e) {
            String expectedErr = "Error parsing OBJ file on line: 1. The .mtl file for taking textures is not " +
                    "specified.";
            assertEquals(expectedErr, e.getMessage());
        }
    }

    @Test
    void parseUntitledMaterial() {
        Model model = new Model();
        model.mtlFileName = "input.mtl";

        List<String> wordsInLine = new ArrayList<>();

        try {
            ObjReader.parseMaterial(model, wordsInLine, 1);
        } catch (ObjReaderException e) {
            String expectedErr = "Error parsing OBJ file on line: 1. The title of the material must be 1 word.";
            assertEquals(expectedErr, e.getMessage());
        }
    }

    @Test
    void parseMaterial() {
        Model model = new Model();
        model.mtlFileName = "input.mtl";

        List<String> wordsInLine = List.of("tree");

        String expected = "tree";
        assertEquals(expected, ObjReader.parseMaterial(model, wordsInLine, 1));
    }
}
