package com.prueba.banco.exception;

public class AccoundNotFoundExcepcion extends RuntimeException {

    public AccoundNotFoundExcepcion(String id) {
        super("No se encontró la cuenta con el id: " + id);
    }

}
