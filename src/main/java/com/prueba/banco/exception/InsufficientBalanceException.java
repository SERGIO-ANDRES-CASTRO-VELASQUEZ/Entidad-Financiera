package com.prueba.banco.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String accountNumber, double balance, double amount) {

        super("La cuenta " + accountNumber + " tiene un saldo insuficiente. Saldo actual: " +
                balance + ", monto solicitado: " + amount);

    }

}
