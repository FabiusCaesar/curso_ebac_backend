package dev.fabiuscaesar.vendasonline.dao.generic;

import dev.fabiuscaesar.vendasonline.domain.Persistente;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.MaisDeUmRegistroException;
import dev.fabiuscaesar.vendasonline.exceptions.TableException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author FabiusCaesar
 * @date 10 de out. de 2025
 */

public interface IGenericDAO<T extends Persistente, E extends Serializable> {

    /** Inserir e devolver a entidade persistida. */
    T cadastrar(T entity) throws TipoChaveNaoEncontradaException, DAOException;

    /** Excluir pelo objeto (padrão JPA). */
    void excluir(T entity) throws DAOException;

    /** Atualizar e devolver a entidade gerenciada. */
    T alterar(T entity) throws TipoChaveNaoEncontradaException, DAOException;

    /** Consultar por ID (entityManager.find). */
    T consultar(E id) throws MaisDeUmRegistroException, TableException, DAOException;

    /** Listar todos. */
    Collection<T> buscarTodos() throws DAOException;
}
