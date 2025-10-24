/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao;

import dev.fabiuscaesar.vendasonline.dao.generic.IGenericDAO;
import dev.fabiuscaesar.vendasonline.domain.Venda;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;

/**
 * @author FabiusCaesar
 * @date 11 de out. de 2025
 */

public interface IVendaDAO extends IGenericDAO<Venda, Long> {

    void finalizarVenda(Venda venda) throws TipoChaveNaoEncontradaException, DAOException;

    void cancelarVenda(Venda venda) throws TipoChaveNaoEncontradaException, DAOException;

    // finders por “codigo” (chave de negócio)
    Venda buscarPorCodigo(String codigo);

    Venda buscarPorCodigoComProdutos(String codigo);
}