package br.com.rpires.exceptions;

/**
 * @author FabiusCaesar
 */
public class EstoqueInsuficienteException extends Exception{

    public EstoqueInsuficienteException(String message) {
        super(message);
    }

    public EstoqueInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
