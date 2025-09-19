package br.com.rpires.services;

import br.com.rpires.dao.IEstoqueDAO;
import br.com.rpires.domain.Estoque;
import br.com.rpires.exceptions.*;
import br.com.rpires.services.generic.GenericService;

/**
 * @author FabiusCaesar
 */
public class EstoqueService extends GenericService<Estoque, Long> implements IEstoqueService {

    private final IEstoqueDAO estoqueDAO;

    public EstoqueService(IEstoqueDAO dao) {
        super(dao);
        this.estoqueDAO = dao;
    }

    @Override
    public void criarSeAusente(Long produtoId) throws DAOException {
        try {
            Estoque e = estoqueDAO.consultar(produtoId);
            if (e == null) {
                estoqueDAO.cadastrar(new Estoque(produtoId, 0));
            }
        } catch (MaisDeUmRegistroException | TableException | TipoChaveNaoEncontradaException e) {
            throw  new DAOException("Erro criando estoque se ausente para produto" + produtoId, e);
        }
    }

    @Override
    public Integer consultarQuantidade(Long produtoId) throws DAOException {
        try {
            Estoque e = estoqueDAO.consultar(produtoId);
            return (e == null) ? 0 : e.getQuantidade();
        } catch (MaisDeUmRegistroException | TableException e) {
            throw new DAOException("Erro consultando quantidade do produto " + produtoId, e);
        }
    }

    @Override
    public void incrementar(Long produtoId, int quantidade) throws DAOException {

        estoqueDAO.incrementar(produtoId, quantidade);

    }

    @Override
    public void decrementarOuFalhar(Long produtoId, int quantidade)
            throws DAOException, EstoqueInsuficienteException{

        estoqueDAO.decrementarOuFalhar(produtoId, quantidade);
    }
}
