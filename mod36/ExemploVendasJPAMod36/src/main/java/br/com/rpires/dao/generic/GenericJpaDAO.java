/**
 * 
 */
package br.com.rpires.dao.generic;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.com.rpires.dao.Persistente;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.MaisDeUmRegistroException;
import br.com.rpires.exceptions.TableException;
import br.com.rpires.exceptions.TipoChaveNaoEncontradaException;
import br.com.rpires.infra.JPAUtil;

/**
 * @author FabiusCaesar
 * @date 13 de set. de 2025
 */

public abstract class GenericJpaDAO<T extends Persistente, K extends Serializable> implements br.com.rpires.dao.generic.IGenericDAO<T, K> {

	private final Class<T> entityClass;
	private final String campoChave;
	
	protected GenericJpaDAO(Class<T> entityClass, String campoChave) {
		this.entityClass = entityClass;
		this.campoChave = campoChave;
	}
	
	@Override
	public Boolean cadastrar(T entity)
	        throws br.com.rpires.exceptions.TipoChaveNaoEncontradaException,
	               br.com.rpires.exceptions.DAOException {
	    EntityManager em = br.com.rpires.infra.JPAUtil.getEntityManager();
	    try {
	        em.getTransaction().begin();
	        em.persist(entity);
	        em.getTransaction().commit();
	        return true;
	    } catch (Exception e) {
	        if (em.getTransaction().isActive()) em.getTransaction().rollback();

	        // Compatibilidade: se for violação de UNIQUE (ex.: CPF duplicado), retorne false
	        if (isUniqueViolation(e)) {
	            return false;
	        }
	        throw new br.com.rpires.exceptions.DAOException(
	                "Erro ao cadastrar " + entity.getClass().getSimpleName(), e);
	    } finally {
	        em.close();
	    }
	}
	
	protected boolean isUniqueViolation(Throwable t) {
	    while (t != null) {
	        // Hibernate -> ConstraintViolationException
	        if (t instanceof org.hibernate.exception.ConstraintViolationException) return true;
	        // PostgreSQL -> SQLState 23505 (unique_violation)
	        if (t instanceof org.postgresql.util.PSQLException) {
	            String state = ((org.postgresql.util.PSQLException) t).getSQLState();
	            if ("23505".equals(state)) return true;
	        }
	        t = t.getCause();
	    }
	    return false;
	}
	
	@Override
	public void alterar(T entity)
	    throws TipoChaveNaoEncontradaException,
	           DAOException {
		EntityManager em = JPAUtil.getEntityManager();
		try {
		    em.getTransaction().begin();
		    em.merge(entity);
		    em.getTransaction().commit();
		} catch (Exception e) {
		    if (em.getTransaction().isActive()) em.getTransaction().rollback();
		    throw new DAOException("Erro ao alterar " + entityClass.getSimpleName(), e);
		} finally {
		    em.close();
		}
	}
	
	@Override
	public void excluir(K valorChave) throws DAOException {
		EntityManager em = JPAUtil.getEntityManager();
		try {
		    em.getTransaction().begin();
		    String jpql = "delete from " + entityClass.getSimpleName() + " e where e." + campoChave + " = :valor";
		    em.createQuery(jpql).setParameter("valor", valorChave).executeUpdate();
		    em.getTransaction().commit();
		} catch (Exception e) {
		    if (em.getTransaction().isActive()) em.getTransaction().rollback();
		    throw new DAOException(
		        "Erro ao excluir " + entityClass.getSimpleName() + " por " + campoChave + "=" + valorChave, e);
		} finally {
		    em.close();
		}
	}
	
	@Override
	public T consultar(K valorChave)
	    throws MaisDeUmRegistroException,
	           TableException,
	           DAOException {
		EntityManager em = JPAUtil.getEntityManager();
		try {
		    String jpql = "select e from " + entityClass.getSimpleName() + " e where e." + campoChave + " = :valor";
		    TypedQuery<T> q = em.createQuery(jpql, entityClass).setParameter("valor", valorChave);
		    List<T> list = q.getResultList();
		    if (list.isEmpty()) return null;
		    if (list.size() > 1) {
		        throw new MaisDeUmRegistroException(
		            "Mais de um registro para " + entityClass.getSimpleName() + "." + campoChave + "=" + valorChave);
		    }
		    return list.get(0);
		} catch (MaisDeUmRegistroException e) {
		    throw e;
		} catch (Exception e) {
		    throw new DAOException(
		        "Erro ao consultar " + entityClass.getSimpleName() + " por " + campoChave + "=" + valorChave, e);
		} finally {
		    em.close();
		}
	}
	
	@Override
	public Collection<T> buscarTodos() throws DAOException {
		EntityManager em = JPAUtil.getEntityManager();
		try {
		    String jpql = "select e from " + entityClass.getSimpleName() + " e";
		    return em.createQuery(jpql, entityClass).getResultList();
		} catch (Exception e) {
		    throw new DAOException("Erro ao buscar todos " + entityClass.getSimpleName(), e);
		} finally {
		    em.close();
		}
	}
}
