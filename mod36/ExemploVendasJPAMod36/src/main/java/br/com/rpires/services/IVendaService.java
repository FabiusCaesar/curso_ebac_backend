package br.com.rpires.services;

/**
 * @author FabiusCaesar
 */
public interface IVendaService {

    void finalizar(String codigoVenda) throws Exception;

    void cancelar(String codigoVenda) throws Exception;
}
