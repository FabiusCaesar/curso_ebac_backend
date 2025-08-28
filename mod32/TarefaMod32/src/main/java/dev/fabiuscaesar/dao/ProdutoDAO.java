/**
 * 
 */
package dev.fabiuscaesar.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import dev.fabiuscaesar.domain.Produto;
import dev.fabiuscaesar.infra.JPAUtil;

/**
 * @author FabiusCaesar
 * @date 27 de ago. de 2025
 */

public class ProdutoDAO implements IProdutoDAO {

	@Override
	public Produto cadastrar(Produto produto) {
		
		EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            em.persist(produto);
            tx.commit();
            return produto;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
	}

	@Override
	public Optional<Produto> buscarPorCodigo(String codigo) {
		
		EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
		
        try {
            String jpql = "SELECT p FROM Produto p WHERE p.codigo = :codigo";
            TypedQuery<Produto> q = em.createQuery(jpql, Produto.class);
            q.setParameter("codigo", codigo);
            Produto unico = q.getSingleResult(); // esperamos 1 por causa do unique
            return Optional.of(unico);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            // Violação grave de integridade (unique quebrado). Logue e repropague.
            throw new IllegalStateException("Mais de um produto com o mesmo código: " + codigo, e);
        } finally {
            em.close();
        }
	}

	@Override
	public boolean excluir(Long id) {
		
		EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            Produto ref = em.getReference(Produto.class, id);                     
            em.remove(ref); // se não existir lança EntityNotFoundException aqui
            tx.commit();
            return true;
            
        } catch (EntityNotFoundException e) {
            if (tx.isActive()) tx.rollback();
            return false; // id inexistente
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
	}

	@Override
	public Optional<Produto> buscarPorId(Long id) {

		var em = JPAUtil.getEntityManagerFactory().createEntityManager();
		
		try {
			Produto ent = em.find(Produto.class, id); // retorna null se não existir
			return Optional.ofNullable(ent);			
		} finally {
			em.close();
		}
	}

	@Override
	public Produto atualizar(Produto produto) {
		
		var em = JPAUtil.getEntityManagerFactory().createEntityManager();
		var tx = em.getTransaction();
		
		try {
			tx.begin();
			Produto gerenciado = em.merge(produto); // aplica mudanças e devolve entidade gerenciada
			tx.commit();
			return gerenciado;			
		} catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
	}

	@Override
	public List<Produto> listarTodos() {
		
		var em = JPAUtil.getEntityManagerFactory().createEntityManager();
		
		try {
            String jpql = "SELECT p FROM Produto p ORDER BY p.id ASC";
                       
            return em.createQuery(jpql, Produto.class).getResultList();
            
        } finally {
            em.close();
        }
		
	}

}
