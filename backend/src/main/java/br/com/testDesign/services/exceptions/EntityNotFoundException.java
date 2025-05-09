package br.com.testDesign.services.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException( ) {
        super("Entity not found");
    }

    public EntityNotFoundException(String messsage) {
        super(messsage);
    }
}
