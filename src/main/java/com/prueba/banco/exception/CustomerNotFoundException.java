package com.prueba.banco.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String id) {
        super("Cliente con el id " + id + "no encontrado");
    }

}
