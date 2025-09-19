package br.com.rpires.services;

import br.com.rpires.domain.Estoque;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.EstoqueInsuficienteException;
import br.com.rpires.services.generic.IGenericService;

/**
 * @author FabiusCaesar
 */
public interface IEstoqueService extends IGenericService<Estoque, Long> {

    void criarSeAusente(Long produtoId) throws DAOException;

    Integer consultarQuantidade(Long produtoId) throws DAOException;

    void incrementar(Long produtoId, int quantidade) throws DAOException;

    void decrementarOuFalhar(Long produtoId, int quantidade)
            throws DAOException, EstoqueInsuficienteException;

}
