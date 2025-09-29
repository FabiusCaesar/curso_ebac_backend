/**
 * 
 */
package br.com.rpires.dao;

import java.time.Instant;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.rpires.dao.generic.GenericJpaDAO;
import br.com.rpires.domain.Estoque;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.EstoqueInsuficienteException;
import br.com.rpires.infra.JPAUtil;

/**
 * @author FabiusCaesar
 * @date 18 de set. de 2025
 */

/**
 * DAO de Estoque usando JPA/Hibernate.
 * PK de Estoque = produto_id (compartilhada com Produto via @MapsId).
 */
public class EstoqueDAO extends GenericJpaDAO<Estoque, Long> implements IEstoqueDAO {

    public EstoqueDAO() {
        // campo-chave para consultar/excluir pelo GenericJpaDAO
        // aqui usamos a PK "id" (que é o produto_id)
        super(Estoque.class, "id");
    }

    @Override
    public void incrementar(Long produtoId, int quantidade) throws DAOException {
        if (quantidade <= 0) return;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // UPDATE atômico no banco (não carrega a entidade)
            Query q = em.createQuery(
                "update Estoque e " +
                "   set e.quantidade = e.quantidade + :q, " +
                "       e.updatedAt  = :now " +
                " where e.id = :id"
            );
            q.setParameter("q", quantidade);
            q.setParameter("now", Instant.now());
            q.setParameter("id", produtoId);

            int rows = q.executeUpdate();
            if (rows == 0) {
                em.getTransaction().rollback();
                throw new DAOException(
                	    "Estoque não encontrado para produto " + produtoId,
                	    new Exception("Nenhuma linha foi atualizada no UPDATE de incremento")
                );
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new DAOException("Erro incrementando estoque do produto " + produtoId, e);
        } finally {
            em.close();
        }
    }

    @Override
    public void decrementarOuFalhar(Long produtoId, int quantidade)
            throws DAOException, EstoqueInsuficienteException {

        if (quantidade <= 0) return;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Guarda de saldo no WHERE para não ficar negativo
            Query q = em.createQuery(
                "update Estoque e " +
                "   set e.quantidade = e.quantidade - :q, " +
                "       e.updatedAt  = :now " +
                " where e.id = :id and e.quantidade >= :q"
            );
            q.setParameter("q", quantidade);
            q.setParameter("now", Instant.now());
            q.setParameter("id", produtoId);

            int rows = q.executeUpdate();
            if (rows == 0) {
                // Verifica se não existe ou se faltou saldo
                Long count = em.createQuery(
                        "select count(e) from Estoque e where e.id = :id", Long.class)
                        .setParameter("id", produtoId)
                        .getSingleResult();

                em.getTransaction().rollback();

                if (count == 0) {
                	throw new DAOException(
                		    "Estoque não encontrado para produto " + produtoId,
                		    new Exception("Nenhuma linha foi atualizada no UPDATE de decremento")
                	);
                } else {
                    throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para produto " + produtoId +
                        " (solicitado=" + quantidade + ")"
                    );
                }
            }

            em.getTransaction().commit();
        } catch (EstoqueInsuficienteException | DAOException e) {
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new DAOException("Erro decrementando estoque do produto " + produtoId, e);
        } finally {
            em.close();
        }
    }
}
