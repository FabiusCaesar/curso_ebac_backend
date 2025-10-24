/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao.generic;

import dev.fabiuscaesar.vendasonline.domain.Persistente;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.MaisDeUmRegistroException;
import dev.fabiuscaesar.vendasonline.exceptions.TableException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;

import javax.persistence.EntityManager;
import javax.inject.Inject;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;

/**
 * DAO genérico JPA (por ID).
 * Mantém apenas o setEntityManager(...) para facilitar testes RESOURCE_LOCAL.
 * 
 * @author FabiusCaesar
 * @date 10 de out. de 2025
 */

public abstract class GenericJpaDAO<T extends Persistente, E extends Serializable>
        implements IGenericDAO<T, E> {

    @Inject
    protected EntityManager entityManager;

    protected final Class<T> persistenteClass;

    protected GenericJpaDAO(Class<T> persistenteClass) {
        this.persistenteClass = persistenteClass;
    }

    /** Para testes (PU RESOURCE_LOCAL / H2). */
    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }

    protected EntityManager em() {
        if (entityManager == null) {
            throw new IllegalStateException("EntityManager não injetado.");
        }
        return entityManager;
    }

    /* =================== CRUD (por ID) =================== */

    @Override
    public T cadastrar(T entity) throws TipoChaveNaoEncontradaException, DAOException {
        try {
            em().persist(entity);
            return entity;
        } catch (RuntimeException e) {
            // Único caso que vale traduzir: violação de unicidade
            if (isUniqueViolation(e)) {
                throw new DAOException("Violação de unicidade ao cadastrar " + persistenteClass.getSimpleName(), e);
            }
            throw e; // deixe o restante subir (mais enxuto; trate no service se quiser)
        }
    }

    @Override
    public void excluir(T entity) throws DAOException {
        if (em().contains(entity)) {
            em().remove(entity);
        } else {
            T managed = em().find(this.persistenteClass, entity.getId());
            if (managed != null) {
                em().remove(managed);
            }
        }
    }

    @Override
    public T alterar(T entity) throws TipoChaveNaoEncontradaException, DAOException {
        return em().merge(entity);
    }

    @Override
    public T consultar(E id) throws MaisDeUmRegistroException, TableException, DAOException {
        return em().find(this.persistenteClass, id);
    }

    @Override
    public Collection<T> buscarTodos() throws DAOException {
        String jpql = "SELECT obj FROM " + this.persistenteClass.getSimpleName() + " obj";
        return em().createQuery(jpql, this.persistenteClass).getResultList();
    }

    /* =================== util =================== */

    protected boolean isUniqueViolation(Throwable t) {
        while (t != null) {
            if (t instanceof SQLIntegrityConstraintViolationException) return true;
            if (t instanceof SQLException && "23505".equals(((SQLException) t).getSQLState())) return true; // Postgres
            if ("org.hibernate.exception.ConstraintViolationException".equals(t.getClass().getName())) return true;
            t = t.getCause();
        }
        return false;
    }
}
