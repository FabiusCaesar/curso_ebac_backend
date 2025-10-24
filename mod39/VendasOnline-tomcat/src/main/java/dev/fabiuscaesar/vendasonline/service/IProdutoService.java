/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service;

import java.util.List;

import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.service.generic.IGenericService;

/**
 * @author FabiusCaesar
 * @date 12 de out. de 2025
 */

public interface IProdutoService extends IGenericService<Produto, Long> {

    List<Produto> filtrarProdutos(String query);

    Produto buscarPorCodigo(String codigo);
}
