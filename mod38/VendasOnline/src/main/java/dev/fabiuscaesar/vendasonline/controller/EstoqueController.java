/**
 * 
 */
package dev.fabiuscaesar.vendasonline.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.EstoqueInsuficienteException;
import dev.fabiuscaesar.vendasonline.service.IEstoqueService;
import dev.fabiuscaesar.vendasonline.service.IProdutoService;

/**
 * @author FabiusCaesar
 * @date 15 de out. de 2025
 */

@Named
@ViewScoped
public class EstoqueController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private IEstoqueService estoqueService;
    @Inject private IProdutoService produtoService;

    private List<Produto> produtos;
    private Map<Long, Integer> quantidades;

    private String codigoBusca;

    // Modal
    private Produto produtoSelecionado;
    private Integer quantidadeInicial;  // snapshot ao abrir o modal
    private Integer quantidadeAtual;    // pré-visualização (vai mudando com "Aplicar")
    private Integer ajusteQuantidade;   // valor digitado (pode ser negativo)

    @PostConstruct
    public void init() {
        produtos    = new ArrayList<>();
        quantidades = new HashMap<>();
        carregarListaEQuantidades();
        limparModal();
    }

    /* ===== lista ===== */
    public void carregarListaEQuantidades() {
        try {
            produtos.clear();
            quantidades.clear();

            List<Produto> todos = new ArrayList<>(produtoService.buscarTodos());
            for (Produto p : todos) {
                produtos.add(p);
                try {
                    Integer q = estoqueService.consultarQuantidade(p.getId());
                    quantidades.put(p.getId(), (q == null ? 0 : q));
                } catch (Exception e) {
                    quantidades.put(p.getId(), 0);
                }
            }
        } catch (Exception e) {
            addMsgErro("Erro ao carregar produtos/estoque: " + causa(e));
        }
    }

    /* ===== modal ===== */
    public void abrirModal(Produto p) {
        if (p == null || p.getId() == null) {
            addMsgErro("Produto inválido.");
            return;
        }
        this.produtoSelecionado = p;

        Integer q = quantidades.get(p.getId());
        if (q == null) {
            try { q = estoqueService.consultarQuantidade(p.getId()); }
            catch (Exception e) { q = 0; }
        }
        this.quantidadeInicial = (q == null ? 0 : q);
        this.quantidadeAtual   = this.quantidadeInicial; // começa igual
        this.ajusteQuantidade  = null;                      // deixa o input vazio ao abrir

        PrimeFaces.current().executeScript("PF('dlgEstoque').show()");
    }

    /** Apenas pré-visualiza: atualiza quantidadeAtual, não persiste. */
    public void aplicarAjuste() {
        if (ajusteQuantidade == null || ajusteQuantidade == 0) {
            addMsgInfo("Informe um ajuste diferente de zero.");
            return;
        }
        int atual = (quantidadeAtual == null ? 0 : quantidadeAtual);
        int novo  = atual + ajusteQuantidade;
        if (novo < 0) {
            addMsgErro("Resultado negativo. Ajuste menor do que o saldo atual.");
            return;
        }
        quantidadeAtual  = novo;
        ajusteQuantidade = null; // limpa o input após aplicar

        // Atualiza apenas o conteúdo do modal
        PrimeFaces.current().ajax().update("formTabela:dlgContent", "growl");
    }

    /** Persiste a diferença (quantidadeAtual - quantidadeInicial) e fecha o modal. */
    public void salvarEFechar() {
        if (produtoSelecionado == null || produtoSelecionado.getId() == null) {
            addMsgErro("Seleção de produto ausente.");
            return;
        }
        int inicial = (quantidadeInicial == null ? 0 : quantidadeInicial);
        int atual   = (quantidadeAtual   == null ? 0 : quantidadeAtual);
        int diff    = atual - inicial;
        if (diff == 0) {
            addMsgInfo("Nada para salvar.");
            PrimeFaces.current().executeScript("PF('dlgEstoque').hide()");
            limparModal();
            return;
        }
        final Long produtoId = produtoSelecionado.getId();

        try {
            if (diff > 0) {
                estoqueService.incrementar(produtoId, diff);
            } else {
                estoqueService.decrementarOuFalhar(produtoId, -diff);
            }

            // Atualiza cache, fecha modal e atualiza tabela
            quantidades.put(produtoId, atual);
            addMsgInfo("Estoque atualizado com sucesso.");

            PrimeFaces.current().ajax().update("formTabela:tabela", "growl");
            PrimeFaces.current().executeScript("PF('dlgEstoque').hide()");
            limparModal();

        } catch (EstoqueInsuficienteException e) {
            addMsgErro("Saldo insuficiente para reduzir estoque.");
        } catch (DAOException e) {
            addMsgErro("Erro de persistência ao salvar estoque: " + causa(e));
        } catch (Exception e) {
            addMsgErro("Falha ao salvar estoque: " + causa(e));
        }
    }

    /** Fecha sem salvar (descarta a pré-visualização). */
    public void cancelarModal() {
        PrimeFaces.current().executeScript("PF('dlgEstoque').hide()");
        limparModal();
    }

    /* ===== busca por código ===== */
    public void buscarPorCodigo() {
        try {
            if (codigoBusca == null || codigoBusca.isBlank()) {
                addMsgInfo("Informe o código do produto para buscar.");
                return;
            }
            Produto p = produtoService.buscarPorCodigo(codigoBusca.trim());
            if (p == null) {
                addMsgInfo("Produto não encontrado para o código informado.");
                return;
            }
            if (produtos.stream().noneMatch(x -> x.getId().equals(p.getId()))) {
                produtos.add(0, p);
            }
            abrirModal(p);
        } catch (Exception e) {
            addMsgErro("Erro na busca por código: " + causa(e));
        }
    }

    /* ===== util ===== */
    public Integer quantidadeDo(Long produtoId) {
        if (produtoId == null) return 0;
        return quantidades.getOrDefault(produtoId, 0);
    }

    private void limparModal() {
        produtoSelecionado = null;
        quantidadeInicial = null;
        quantidadeAtual = null;
        ajusteQuantidade = null;
    }

    private void addMsgInfo(String m) {
        javax.faces.context.FacesContext.getCurrentInstance()
            .addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, m, null));
    }
    private void addMsgErro(String m) {
        javax.faces.context.FacesContext.getCurrentInstance()
            .addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, m, null));
    }
    private String causa(Throwable t) {
        return (t.getCause() != null ? t.getCause().getMessage() : t.getMessage());
    }

    /* getters/setters */
    public List<Produto> getProdutos() { return produtos; }
    public Map<Long, Integer> getQuantidades() { return quantidades; }

    public String getCodigoBusca() { return codigoBusca; }
    public void setCodigoBusca(String codigoBusca) { this.codigoBusca = codigoBusca; }

    public Produto getProdutoSelecionado() { return produtoSelecionado; }
    public void setProdutoSelecionado(Produto produtoSelecionado) { this.produtoSelecionado = produtoSelecionado; }

    public Integer getQuantidadeInicial() { return quantidadeInicial; }
    public void setQuantidadeInicial(Integer quantidadeInicial) { this.quantidadeInicial = quantidadeInicial; }

    public Integer getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(Integer quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }

    public Integer getAjusteQuantidade() { return ajusteQuantidade; }
    public void setAjusteQuantidade(Integer ajusteQuantidade) { this.ajusteQuantidade = ajusteQuantidade; }
}
