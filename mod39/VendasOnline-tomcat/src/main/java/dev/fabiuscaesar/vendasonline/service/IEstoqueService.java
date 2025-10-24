/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service;

import dev.fabiuscaesar.vendasonline.domain.Estoque;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.EstoqueInsuficienteException;
import dev.fabiuscaesar.vendasonline.service.generic.IGenericService;

/**
 * @author FabiusCaesar
 * @date 13 de out. de 2025
 */

public interface IEstoqueService extends IGenericService<Estoque, Long> {

    void criarSeAusente(Long produtoId) throws DAOException;

    Integer consultarQuantidade(Long produtoId) throws DAOException;

    void incrementar(Long produtoId, int quantidade) throws DAOException;

    void decrementarOuFalhar(Long produtoId, int quantidade)
            throws DAOException, EstoqueInsuficienteException;

}