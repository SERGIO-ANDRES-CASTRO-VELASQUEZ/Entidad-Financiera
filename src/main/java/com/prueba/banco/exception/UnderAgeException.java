package com.prueba.banco.exception;

public class UnderAgeException extends RuntimeException {

    public UnderAgeException() {

        super("El cliente debe ser mayor de edad para crear una cuenta");

    }

}
