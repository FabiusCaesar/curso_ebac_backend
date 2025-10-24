/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao;

import javax.enterprise.context.ApplicationScoped;
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

@ApplicationScoped
public class EstoqueDAO extends GenericJpaDAO<Estoque, Long> implements IEstoqueDAO {

    public EstoqueDAO() {
        super(Estoque.class);
    }

    /** Consulta por produtoId (não pelo id do estoque). */
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

    /**
     * UPSERT: tenta UPDATE; se afetar 0 linhas, faz INSERT (quantidade inicial = quantidade informada).
     * Se houver disputa de concorrência (UK), cai num segundo UPDATE.
     */
    @Override
    public void incrementar(Long produtoId, int quantidade) throws DAOException {
        if (quantidade <= 0) return;

        try {
            Instant agora = Instant.now();

            Query q = em().createQuery(
                    "update Estoque e set e.quantidade = e.quantidade + :q, e.updatedAt = :agora " +
                    "where e.produto.id = :pid");
            q.setParameter("q", quantidade);
            q.setParameter("agora", agora);
            q.setParameter("pid", produtoId);

            int updated = q.executeUpdate();

            if (updated == 0) {
                // não existe -> tentar inserir
                try {
                    Produto ref = em().getReference(Produto.class, produtoId);
                    Estoque novo = new Estoque(ref, quantidade);
                    novo.setUpdatedAt(agora);
                    em().persist(novo);
                    em().flush();
                } catch (RuntimeException ex) {
                    // se outra thread inseriu, repetimos o UPDATE
                    if (isUniqueViolation(ex)) {
                        int retry = em().createQuery(
                                "update Estoque e set e.quantidade = e.quantidade + :q, e.updatedAt = :agora " +
                                "where e.produto.id = :pid")
                                .setParameter("q", quantidade)
                                .setParameter("agora", agora)
                                .setParameter("pid", produtoId)
                                .executeUpdate();
                        if (retry == 0) {
                            throw new DAOException("Falha no upsert do estoque (produtoId=" + produtoId + ")", ex);
                        }
                    } else {
                        throw ex;
                    }
                }
            } else {
                em().flush();
            }

            em().clear();
        } catch (RuntimeException e) {
            throw new DAOException("Erro ao incrementar estoque", e);
        }
    }

    @Override
    public void decrementarOuFalhar(Long produtoId, int quantidade)
            throws DAOException, EstoqueInsuficienteException {
        if (quantidade <= 0) return;

        try {
            Instant agora = Instant.now();

            Query q = em().createQuery(
                    "update Estoque e set e.quantidade = e.quantidade - :q, e.updatedAt = :agora " +
                    "where e.produto.id = :pid and e.quantidade >= :q");
            q.setParameter("q", quantidade);
            q.setParameter("agora", agora);
            q.setParameter("pid", produtoId);

            int updated = q.executeUpdate();
            em().flush();
            em().clear();

            if (updated == 0) {
                // motivo: inexistente ou saldo insuficiente
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
