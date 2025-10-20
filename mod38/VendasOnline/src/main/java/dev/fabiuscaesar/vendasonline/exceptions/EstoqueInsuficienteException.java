package dev.fabiuscaesar.vendasonline.exceptions;

/**
 * @author FabiusCaesar
 */
public class EstoqueInsuficienteException extends Exception{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EstoqueInsuficienteException(String message) {
        super(message);
    }

    public EstoqueInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
