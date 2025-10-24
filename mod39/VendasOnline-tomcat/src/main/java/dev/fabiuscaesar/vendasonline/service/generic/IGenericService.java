/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service.generic;

import java.io.Serializable;
import java.util.Collection;

import dev.fabiuscaesar.vendasonline.domain.Persistente;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.MaisDeUmRegistroException;
import dev.fabiuscaesar.vendasonline.exceptions.TableException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;

/**
 * @author FabiusCaesar
 * @date 12 de out. de 2025
 */

/** Contrato genérico alinhado ao padrão por ID. */
public interface IGenericService<T extends Persistente, E extends Serializable> {

    T cadastrar(T entity) throws TipoChaveNaoEncontradaException, DAOException;

    void excluir(T entity) throws DAOException;

    T alterar(T entity) throws TipoChaveNaoEncontradaException, DAOException;

    T consultar(E valor) throws MaisDeUmRegistroException, TableException, DAOException;

    Collection<T> buscarTodos() throws DAOException;
}
