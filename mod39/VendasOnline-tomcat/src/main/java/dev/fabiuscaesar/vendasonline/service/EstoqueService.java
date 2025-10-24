/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service;

import dev.fabiuscaesar.vendasonline.config.Transactional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dev.fabiuscaesar.vendasonline.dao.IEstoqueDAO;
import dev.fabiuscaesar.vendasonline.domain.Estoque;
import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.exceptions.*;
import dev.fabiuscaesar.vendasonline.service.generic.GenericService;

/**
 * @author FabiusCaesar
 * @date 13 de out. de 2025
 */

@ApplicationScoped
@Transactional
public class EstoqueService extends GenericService<Estoque, Long> implements IEstoqueService {

    private IEstoqueDAO estoqueDAO;

    /** Construtor NO-ARGS exigido pelo proxy do CDI (Weld/Tomcat). */
    protected EstoqueService() {
        super();
    }

    @Inject
    public EstoqueService(IEstoqueDAO estoqueDAO) {
        super(estoqueDAO);
        this.estoqueDAO = estoqueDAO;
    }

    @Override
    public void criarSeAusente(Long produtoId) throws DAOException {
        try {
            Estoque e = estoqueDAO.consultar(produtoId);
            if (e == null) {
                Produto p = new Produto();
                p.setId(produtoId);
                Estoque novo = new Estoque(p, 0);
                estoqueDAO.cadastrar(novo);
            }
        } catch (MaisDeUmRegistroException | TableException | TipoChaveNaoEncontradaException ex) {
            throw new DAOException("Erro criando estoque para produto " + produtoId, ex);
        }
    }

    @Override
    public Integer consultarQuantidade(Long produtoId) throws DAOException {
        try {
            Estoque e = estoqueDAO.consultar(produtoId);
            return (e == null ? 0 : e.getQuantidade());
        } catch (MaisDeUmRegistroException | TableException ex) {
            throw new DAOException("Erro consultando quantidade do produto " + produtoId, ex);
        }
    }

    @Override
    public void incrementar(Long produtoId, int quantidade) throws DAOException {
        // por garantia, mantém o "create-if-absent" (o DAO já faz UPSERT também)
        criarSeAusente(produtoId);
        estoqueDAO.incrementar(produtoId, quantidade);
    }

    @Override
    public void decrementarOuFalhar(Long produtoId, int quantidade)
            throws DAOException, EstoqueInsuficienteException {
        estoqueDAO.decrementarOuFalhar(produtoId, quantidade);
    }
}
