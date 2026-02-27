package com.prueba.banco.exception;

public class HasProductsException extends RuntimeException {

    public HasProductsException(String id) {

        super("El cliente con el id " + id + "tiene productos asociados, no se puede eliminar");

    }

}
