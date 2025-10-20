/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao;

import dev.fabiuscaesar.vendasonline.dao.generic.IGenericDAO;
import dev.fabiuscaesar.vendasonline.domain.Estoque;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.EstoqueInsuficienteException;

/**
 * @author FabiusCaesar
 * @date 12 de out. de 2025
 */

public interface IEstoqueDAO extends IGenericDAO<Estoque, Long> {

    void incrementar(Long produtoId, int quantidade) throws DAOException;

    void decrementarOuFalhar(Long produtoId, int quantidade)
    		throws DAOException, EstoqueInsuficienteException;

}
