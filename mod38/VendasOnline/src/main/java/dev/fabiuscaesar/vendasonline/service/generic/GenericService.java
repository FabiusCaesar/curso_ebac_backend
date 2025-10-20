/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service.generic;

import java.io.Serializable;
import java.util.Collection;

import dev.fabiuscaesar.vendasonline.dao.generic.IGenericDAO;
import dev.fabiuscaesar.vendasonline.domain.Persistente;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.MaisDeUmRegistroException;
import dev.fabiuscaesar.vendasonline.exceptions.TableException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;

/**
 * @author FabiusCaesar
 * @date 12 de out. de 2025
 */

public abstract class GenericService<T extends Persistente, E extends Serializable>
        implements IGenericService<T, E> {

    protected final IGenericDAO<T, E> dao;

    protected GenericService(IGenericDAO<T, E> dao) {
        this.dao = dao;
    }

    @Override
    public T cadastrar(T entity) throws TipoChaveNaoEncontradaException, DAOException {
        return dao.cadastrar(entity);
    }

    @Override
    public void excluir(T entity) throws DAOException {
        dao.excluir(entity);
    }

    @Override
    public T alterar(T entity) throws TipoChaveNaoEncontradaException, DAOException {
        return dao.alterar(entity);
    }

    @Override
    public T consultar(E valor) throws MaisDeUmRegistroException, TableException, DAOException {
        return dao.consultar(valor);
    }

    @Override
    public Collection<T> buscarTodos() throws DAOException {
        return dao.buscarTodos();
    }
}
