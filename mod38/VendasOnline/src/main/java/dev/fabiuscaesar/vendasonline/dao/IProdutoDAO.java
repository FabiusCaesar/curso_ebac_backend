/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao;

import java.util.List;

import dev.fabiuscaesar.vendasonline.dao.generic.IGenericDAO;
import dev.fabiuscaesar.vendasonline.domain.Produto;

/**
 * @author FabiusCaesar
 * @date 11 de out. de 2025
 */

public interface IProdutoDAO extends IGenericDAO<Produto, Long> {

    List<Produto> filtrarProdutos(String query);

    // finder de negócio (já que o genérico é por ID)
    Produto buscarPorCodigo(String codigo);
}