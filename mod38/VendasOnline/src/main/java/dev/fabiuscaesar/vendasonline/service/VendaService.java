/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import dev.fabiuscaesar.vendasonline.dao.IVendaDAO;
import dev.fabiuscaesar.vendasonline.domain.ProdutoQuantidade;
import dev.fabiuscaesar.vendasonline.domain.Venda;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.EstoqueInsuficienteException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;
import dev.fabiuscaesar.vendasonline.service.generic.GenericService;

/**
 * @author FabiusCaesar
 * @date 13 de out. de 2025
 */

@Stateless
public class VendaService extends GenericService<Venda, Long> implements IVendaService {

    private final IVendaDAO vendaDAO;
    private final IEstoqueService estoqueService;

    @Inject
    public VendaService(IVendaDAO vendaDAO, IEstoqueService estoqueService) {
        super(vendaDAO);
        this.vendaDAO = vendaDAO;
        this.estoqueService = estoqueService;
    }
    
    // (2) Construtor sem argumentos para satisfazer o contrato do EJB
    protected VendaService() {
        super(null);      // não será usado; o @Inject acima é o construtor efetivo
        this.vendaDAO = null;
        this.estoqueService = null;
    }

    @Override
    public void finalizar(String codigoVenda) throws DAOException, TipoChaveNaoEncontradaException, EstoqueInsuficienteException {
        final Venda venda = buscarPorCodigo(codigoVenda);
        if (venda == null) throw new IllegalArgumentException("Venda não encontrada: " + codigoVenda);
        if (venda.getStatus() == Venda.Status.CONCLUIDA) {
            throw new UnsupportedOperationException("Venda já finalizada");
        }
        if (venda.getStatus() == Venda.Status.CANCELADA) {
            throw new UnsupportedOperationException("Venda cancelada não pode ser finalizada");
        }

        // baixa estoque
        for (ProdutoQuantidade pq : venda.getProdutos()) {
            estoqueService.decrementarOuFalhar(pq.getProduto().getId(), pq.getQuantidade());
        }

        vendaDAO.finalizarVenda(venda);
    }

    @Override
    public void cancelar(String codigo) throws DAOException {
        // Busca a venda com seus itens
        Venda v = vendaDAO.buscarPorCodigoComProdutos(codigo);
        if (v == null) throw new DAOException("Venda não encontrada: " + codigo, null);

        boolean eraConcluida = v.getStatus() == Venda.Status.CONCLUIDA;

        // Se estava concluída, estorna o estoque dos itens
        if (eraConcluida && v.getProdutos() != null) {
            for (ProdutoQuantidade pq : v.getProdutos()) {
                try {
                    estoqueService.incrementar(pq.getProduto().getId(), pq.getQuantidade());
                } catch (Exception e) {
                    // mantém contrato do service expondo DAOException
                    throw new DAOException("Erro ao estornar estoque", e);
                }
            }
        }

        // Marca como CANCELADA (DAO pode lançar TipoChaveNaoEncontradaException)
        try {
            vendaDAO.cancelarVenda(v);
        } catch (TipoChaveNaoEncontradaException e) {
            throw new DAOException("Erro ao cancelar venda", e);
        }
    }
    
    @Override
    public Venda buscarPorCodigo(String codigo) throws DAOException {
        return vendaDAO.buscarPorCodigoComProdutos(codigo);
    }
}

