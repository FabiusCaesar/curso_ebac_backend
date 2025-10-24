/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao;


import javax.enterprise.context.ApplicationScoped;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import dev.fabiuscaesar.vendasonline.dao.generic.GenericJpaDAO;
import dev.fabiuscaesar.vendasonline.domain.Produto;

/**
 * @author FabiusCaesar
 * @date 11 de out. de 2025
 */

@ApplicationScoped
public class ProdutoDAO extends GenericJpaDAO<Produto, Long> implements IProdutoDAO {

    public ProdutoDAO() {
        super(Produto.class);
    }

    @Override
    public List<Produto> filtrarProdutos(String query) {
        TypedQuery<Produto> tpQuery =
            this.entityManager.createNamedQuery("Produto.findByNome", this.persistenteClass);
        tpQuery.setParameter("nome", "%" + query + "%");
        return tpQuery.getResultList();
    }

    @Override
    public Produto buscarPorCodigo(String codigo) {
        try {
            return this.entityManager
                    .createNamedQuery("Produto.findByCodigo", Produto.class)
                    .setParameter("codigo", codigo)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
