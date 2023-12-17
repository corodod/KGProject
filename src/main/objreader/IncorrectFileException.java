package main.objreader;

public class IncorrectFileException extends Exception{
    public IncorrectFileException(String additionString){
        super("Input file is incorrect. " + additionString);
    }
}