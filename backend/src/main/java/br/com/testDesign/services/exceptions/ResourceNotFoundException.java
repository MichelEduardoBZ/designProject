package br.com.testDesign.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException( ) {
        super("Entity not found");
    }

    public ResourceNotFoundException(String messsage) {
        super(messsage);
    }
}
