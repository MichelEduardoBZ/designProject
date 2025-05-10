package br.com.testDesign.services.exceptions;

public class DatabaseException extends RuntimeException {

    public DatabaseException() {
        super("Database exception");
    }

    public DatabaseException(String messsage) {
        super(messsage);
    }
}
