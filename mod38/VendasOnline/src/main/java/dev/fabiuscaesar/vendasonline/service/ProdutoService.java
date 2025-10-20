/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import dev.fabiuscaesar.vendasonline.dao.IProdutoDAO;
import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.service.generic.GenericService;

/**
 * @author FabiusCaesar
 * @date 12 de out. de 2025
 */

@Stateless
public class ProdutoService extends GenericService<Produto, Long> implements IProdutoService {

    private final IProdutoDAO produtoDAO;

    @Inject
    public ProdutoService(IProdutoDAO produtoDAO) {
        super(produtoDAO);
        this.produtoDAO = produtoDAO;
    }

    @Override
    public List<Produto> filtrarProdutos(String query) {
        return produtoDAO.filtrarProdutos(query);
    }

    @Override
    public Produto buscarPorCodigo(String codigo) {
        return produtoDAO.buscarPorCodigo(codigo);
    }
}