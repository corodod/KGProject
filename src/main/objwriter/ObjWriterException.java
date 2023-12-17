package main.objwriter;

public class ObjWriterException extends RuntimeException {
    public ObjWriterException(String errorMessage) {
        super("Error writing OBJ file:" + errorMessage);
    }
}
