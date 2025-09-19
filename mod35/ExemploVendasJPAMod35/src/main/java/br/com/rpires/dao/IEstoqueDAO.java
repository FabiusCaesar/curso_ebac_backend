package br.com.rpires.dao;

import br.com.rpires.dao.generic.IGenericDAO;
import br.com.rpires.domain.Estoque;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.EstoqueInsuficienteException;

/**
 * @author FabiusCaesar
 */
public interface IEstoqueDAO extends IGenericDAO<Estoque, Long> {

    void incrementar(Long produtoId, int quantidade) throws DAOException;

    void decrementarOuFalhar(Long produtoId, int quantidade) throws DAOException, EstoqueInsuficienteException;

}
