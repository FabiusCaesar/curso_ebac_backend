package br.com.rpires.services;

import br.com.rpires.dao.IVendaDAO;
import br.com.rpires.domain.ProdutoQuantidade;
import br.com.rpires.domain.Venda;
import br.com.rpires.services.generic.GenericService;

/**
 * @author FabiusCaesar
 */
public class VendaService extends GenericService<Venda, String> implements IVendaService {

    private final IVendaDAO vendaDAO;
    private final IEstoqueService estoqueService;

    // construtor para injeção de dependências
    public VendaService(IVendaDAO vendaDAO, IEstoqueService estoqueService) {
        super(vendaDAO);
        this.vendaDAO = vendaDAO;
        this.estoqueService = estoqueService;
    }

    @Override
    public void finalizar(String codigoVenda) throws Exception {
        // 1) buscar
        Venda venda = vendaDAO.consultar(codigoVenda);

        // 2) validar inexistente
        if ( venda == null) {
            throw new IllegalArgumentException("Venda não encontrada: " + codigoVenda);
        }

        // 3) validar status atual
        if (venda.getStatus() == Venda.Status.CONCLUIDA) {
            throw new UnsupportedOperationException("Venda já finalizada");
        }

        if (venda.getStatus() == Venda.Status.CANCELADA) {
            throw new UnsupportedOperationException("Venda cancelada não pode ser finalizada");
        }

        for (ProdutoQuantidade pq : venda.getProdutos()) {
            Long produtoId = pq.getProduto().getId();
            int quantidade = pq.getQuantidade();
            estoqueService.decrementarOuFalhar(produtoId, quantidade);
        }

        vendaDAO.finalizarVenda(venda);
    }

    @Override
    public void cancelar(String codigoVenda) throws Exception {
        // 1) buscar
        Venda venda = vendaDAO.consultar(codigoVenda);

        // 2) validar inexistente
        if ( venda == null)
            throw new IllegalArgumentException("Venda não encontrada: " + codigoVenda);

        // 3) regras de status
        if (venda.getStatus() == Venda.Status.CANCELADA)
            throw new UnsupportedOperationException("Venda já cancelada");

        if (venda.getStatus() == Venda.Status.INICIADA)
            throw new UnsupportedOperationException("Só é possível cancelar vendas já finalizadas");

        // 4) devolver estoque item a item
        for (ProdutoQuantidade pq : venda.getProdutos()) {
            Long produtoId = pq.getProduto().getId();
            int quantidade = pq.getQuantidade();
            estoqueService.incrementar(produtoId, quantidade);
        }

        // 4) persistir o cancelamento
        vendaDAO.cancelarVenda(venda);
    }
}
