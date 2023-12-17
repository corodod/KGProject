package main.objwriter;

import main.math.Vector2f;
import main.math.Vector3f;
import main.model.Model;
import main.model.Polygon;
import main.objreader.ObjReaderException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class ObjWriter {

    public static void write(String fileName, Model model) {
        File file = new File(fileName);

        try {
            if (file.createNewFile()) {
                System.out.println("Файл успешно создан: " + file.getName());
            } else {
                System.out.println("Файл уже существует.");
            }
        } catch (IOException e) {
            throw new ObjWriterException(e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writeVertices(writer, model.vertices);
            writeTextureVertices(writer, model.textureVertices);
            writeNormals(writer, model.normals);
            writePolygons(writer, model.polygons);
        } catch (IOException e) {
            throw new ObjWriterException(e.getMessage());
        }
    }


    private static void writeVertices(BufferedWriter writer, List<Vector3f> vertices) throws IOException {
        DecimalFormatSymbols customSymbols = new DecimalFormatSymbols(Locale.US);
        customSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("0.######", customSymbols);

        for (Vector3f vertex : vertices) {
            writer.write("v " + decimalFormat.format(vertex.x) + " " + decimalFormat.format(vertex.y) + " " + decimalFormat.format(vertex.z));
            writer.newLine();
        }
    }

    private static void writeTextureVertices(BufferedWriter writer, List<Vector2f> textureVertices) throws IOException {
        DecimalFormatSymbols customSymbols = new DecimalFormatSymbols(Locale.US);
        customSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("0.######", customSymbols);

        for (Vector2f textureVertex : textureVertices) {
            writer.write("vt " + decimalFormat.format(textureVertex.x) + " " + decimalFormat.format(textureVertex.y));
            writer.newLine();
        }
    }

    private static void writeNormals(BufferedWriter writer, List<Vector3f> normals) throws IOException {
        DecimalFormatSymbols customSymbols = new DecimalFormatSymbols(Locale.US);
        customSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("0.######", customSymbols);

        for (Vector3f normal : normals) {
            writer.write("vn " + decimalFormat.format(normal.x) + " " + decimalFormat.format(normal.y) + " " + decimalFormat.format(normal.z));
            writer.newLine();
        }
    }
    private static void writePolygons(BufferedWriter writer, List<Polygon> polygons) throws IOException {
        for (Polygon polygon : polygons) {
            writer.write("f ");
            List<Integer> vertexIndices = polygon.getVertexIndices();
            List<Integer> textureVertexIndices = polygon.getTextureVertexIndices();
            List<Integer> normalIndices = polygon.getNormalIndices();

            for (int i = 0; i < vertexIndices.size(); i++) {
                if(!textureVertexIndices.isEmpty()){
                    writer.write(vertexIndices.get(i) + 1 + "/" + (textureVertexIndices.get(i) + 1));
                }
                else{
                    writer.write(String.valueOf(vertexIndices.get(i) + 1));
                }
                if (!normalIndices.isEmpty()) {
                    if(textureVertexIndices.isEmpty()){
                        writer.write("/");
                    }
                    writer.write("/" + (normalIndices.get(i) + 1));
                }
                writer.write(" ");
            }
            writer.newLine();
        }
    }
}
