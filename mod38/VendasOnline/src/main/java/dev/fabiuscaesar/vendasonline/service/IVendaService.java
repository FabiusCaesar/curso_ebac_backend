/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service;

import dev.fabiuscaesar.vendasonline.domain.Venda;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.EstoqueInsuficienteException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;
import dev.fabiuscaesar.vendasonline.service.generic.IGenericService;

/**
 * @author FabiusCaesar
 * @date 13 de out. de 2025
 */

public interface IVendaService extends IGenericService<Venda, Long> {
	
    void finalizar(String codigoVenda)
            throws DAOException, TipoChaveNaoEncontradaException, EstoqueInsuficienteException;

    void cancelar(String codigoVenda)
            throws DAOException, TipoChaveNaoEncontradaException;
    
    Venda buscarPorCodigo(String codigo) throws DAOException;
}