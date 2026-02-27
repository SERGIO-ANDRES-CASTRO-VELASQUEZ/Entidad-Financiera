package com.prueba.banco.exception;

public class AccountStatusException extends  RuntimeException {

    public AccountStatusException(String accountNumber, String typeStatus) {

        super("La cuenta " + accountNumber + " no se encuentra en estado " +
                typeStatus + ", no se puede realizar la operación");

    }

}
