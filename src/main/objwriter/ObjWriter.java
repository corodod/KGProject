package main.objwriter;

import main.objtoken.ObjToken;
import main.math.Vector2f;
import main.math.Vector3f;
import main.model.Model;



public class ObjWriter {
    private Model model;

    public ObjWriter(Model model) {
        this.model = model;
    }


    public void setModel(Model model) {
        this.model = model;
    }

    public static String getContent(Model model) {
        ObjWriter writer = new ObjWriter(model);
        return writer.writeObjFile();
    }

    public String writeObjFile() {
        final var lines = new StringBuilder();

        this.appendVerticesInLines(lines);
        this.appendTexturesInLines(lines);
        this.appendNormalsInLines(lines);
        this.appendFacesInLines(lines);

        return lines.toString();
    }

    private void appendVerticesInLines(StringBuilder lines) {
        if (this.model.vertices.size() == 0) {
            throw new IllegalStateException("Can not make an object with out vertices");
        } else {
            final var vertices = this.model.vertices;

            for (Vector3f vertex : vertices) {
                lines.append(this.getVector3fWithTokenInString(vertex, ObjToken.VERTEX)).append("\n");
            }
        }
    }

    private void appendTexturesInLines(StringBuilder lines) {
        if (this.model.textureVertices != null) {
            final var textures = this.model.textureVertices;

            for (Vector2f texture : textures) {
                lines.append(this.getVector2fWithTokenInString(texture)).append("\n");
            }
        }
    }

    private void appendNormalsInLines(StringBuilder lines) {
        if (this.model.normals != null) {
            final var normals = this.model.normals;

            for (Vector3f normal : normals) {
                lines.append(this.getVector3fWithTokenInString(normal, ObjToken.NORMAL)).append("\n");
            }
        }
    }

    private void appendFacesInLines(StringBuilder lines) {
        if (this.model.polygons == null) {
            throw new IllegalArgumentException("Can not make an object with out polygons");
        } else {
            final var polygons = this.model.polygons;

            for (int i = 0; i < polygons.size(); ++i) {
                final var polygon = polygons.get(i);
                lines.append(ObjToken.FACE).append(" ");

                for (int index = 0; index < polygon.getVertexIndices().size(); ++index) {
                    lines.append(this.getNumberInString((float) (polygon.getVertexIndices().get(index) + 1)));
                    Integer textureIndex = null;
                    if (polygon.getTextureVertexIndices().size() > 0) {
                        textureIndex = polygon.getTextureVertexIndices().get(index) + 1;
                        lines.append("/").append(this.getNumberInString(textureIndex)).append(" ");
                    }

                    if (polygon.getNormalIndices().size() > 0) {
                        if (textureIndex == null) {
                            lines.append("/");
                        }

                        lines.append("/").append(this.getNumberInString((float) (polygon.getNormalIndices().get(index) + 1)));
                    }

                    if (index < polygon.getVertexIndices().size() - 1) {
                        lines.append(" ");
                    }
                }

                if (i < polygons.size() - 1) {
                    lines.append("\n");
                }
            }
        }
    }

    protected String getVector3fWithTokenInString(Vector3f vector3f, ObjToken token) {
        return token + " " + this.getNumberInString(vector3f.x) + " " + this.getNumberInString(vector3f.y) + " " + this.getNumberInString(vector3f.z);
    }

    protected String getVector2fWithTokenInString(Vector2f vector2f) {
        return ObjToken.TEXTURE + " " + this.getNumberInString(vector2f.x) + " " + this.getNumberInString(vector2f.y);
    }

    protected String getNumberInString(double number) {
        String result = "";
        if (number % 1.0F == 0.0F) {
            result = result + (int) number;
        } else {
            result = result + number;
        }

        return result;
    }

    protected String getNumberInString(Integer number) {
        return number.toString();
    }
}
