/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao;

import java.time.Instant;

import javax.persistence.Query;

import dev.fabiuscaesar.vendasonline.dao.generic.GenericJpaDAO;
import dev.fabiuscaesar.vendasonline.domain.Estoque;
import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.EstoqueInsuficienteException;
import dev.fabiuscaesar.vendasonline.exceptions.MaisDeUmRegistroException;
import dev.fabiuscaesar.vendasonline.exceptions.TableException;

/**
 * @author FabiusCaesar
 * @date 12 de out. de 2025
 */

public class EstoqueDAO extends GenericJpaDAO<Estoque, Long> implements IEstoqueDAO {

    public EstoqueDAO() {
        super(Estoque.class); // GenericJpaDAO slim: só 1 parâmetro
    }

    /** Mantém a consulta por produtoId (negócio), não pelo id do estoque. */
    @Override
    public Estoque consultar(Long produtoId)
            throws MaisDeUmRegistroException, TableException, DAOException {
        try {
            return em().createQuery(
                    "select e from Estoque e where e.produto.id = :pid", Estoque.class)
                .setParameter("pid", produtoId)
                .getResultStream()
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            throw new DAOException("Erro ao consultar Estoque por produtoId=" + produtoId, e);
        }
    }

    @Override
    public Estoque cadastrar(Estoque entity) throws DAOException {
        try {
            if (entity.getProduto() != null && entity.getProduto().getId() != null) {
                entity.setProduto(em().getReference(Produto.class, entity.getProduto().getId()));
            }
            em().persist(entity);
            em().flush(); // valida UK (id_produto) agora
            return entity;
        } catch (RuntimeException e) {
            if (isUniqueViolation(e)) {
                throw new DAOException("Já existe registro de estoque para este produto.", e);
            }
            throw new DAOException("Erro ao cadastrar Estoque.", e);
        }
    }

    @Override
    public void incrementar(Long produtoId, int quantidade) throws DAOException {
        if (quantidade <= 0) return; // requisito: não altera se qtd <= 0
        try {
            Query q = em().createQuery(
                "update Estoque e set e.quantidade = e.quantidade + :q, e.updatedAt = :agora " +
                "where e.produto.id = :pid");
            q.setParameter("q", quantidade);
            q.setParameter("agora", Instant.now());
            q.setParameter("pid", produtoId);

            int updated = q.executeUpdate();
            em().flush();
            em().clear();

            if (updated == 0) {
                throw new DAOException("Estoque não encontrado para produtoId=" + produtoId, null);
            }
        } catch (RuntimeException e) {
            throw new DAOException("Erro ao incrementar estoque", e);
        }
    }

    @Override
    public void decrementarOuFalhar(Long produtoId, int quantidade)
            throws DAOException, EstoqueInsuficienteException {
        if (quantidade <= 0) return; // requisito: não altera se qtd <= 0
        try {
            Query q = em().createQuery(
                "update Estoque e set e.quantidade = e.quantidade - :q, e.updatedAt = :agora " +
                "where e.produto.id = :pid and e.quantidade >= :q");
            q.setParameter("q", quantidade);
            q.setParameter("agora", Instant.now());
            q.setParameter("pid", produtoId);

            int updated = q.executeUpdate();
            em().flush();
            em().clear();

            if (updated == 0) {
                // ver motivo: inexistente ou saldo insuficiente
                Estoque e = em().createQuery(
                        "select e from Estoque e where e.produto.id = :pid", Estoque.class)
                    .setParameter("pid", produtoId)
                    .getResultStream().findFirst().orElse(null);

                if (e == null) {
                    throw new DAOException("Estoque não encontrado para produtoId=" + produtoId, null);
                }
                throw new EstoqueInsuficienteException("Saldo insuficiente para produtoId=" + produtoId);
            }
        } catch (EstoqueInsuficienteException ex) {
            throw ex;
        } catch (RuntimeException e) {
            throw new DAOException("Erro ao decrementar estoque", e);
        }
    }
}

