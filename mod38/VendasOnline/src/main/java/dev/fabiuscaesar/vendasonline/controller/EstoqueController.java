/**
 * 
 */
package dev.fabiuscaesar.vendasonline.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import dev.fabiuscaesar.vendasonline.service.IEstoqueService;

/**
 * @author FabiusCaesar
 * @date 15 de out. de 2025
 */

@Named
@ViewScoped
public class EstoqueController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject private IEstoqueService estoqueService;

    private Long produtoId;
    private Integer quantidade;      // usada para inc/dec
    private Integer quantidadeAtual; // exibida na tela

    @PostConstruct
    public void init() {
        quantidade = 0;
        quantidadeAtual = null;
    }

    public void consultar() {
        try {
            quantidadeAtual = estoqueService.consultarQuantidade(produtoId);
            addMsg("Quantidade atual: " + quantidadeAtual);
        } catch (Exception e) {
            addMsg("Erro ao consultar quantidade");
        }
    }

    public void criarSeAusente() {
        try {
            estoqueService.criarSeAusente(produtoId);
            addMsg("Estoque criado (se não existia)");
            consultar();
        } catch (Exception e) {
            addMsg("Erro ao criar estoque");
        }
    }

    public void incrementar() {
        try {
            estoqueService.incrementar(produtoId, quantidade == null ? 0 : quantidade);
            consultar();
            addMsg("Estoque incrementado");
        } catch (Exception e) {
            addMsg("Erro ao incrementar estoque");
        }
    }

    public void decrementar() {
        try {
            estoqueService.decrementarOuFalhar(produtoId, quantidade == null ? 0 : quantidade);
            consultar();
            addMsg("Estoque decrementado");
        } catch (Exception e) {
            addMsg("Erro ao decrementar estoque: " + e.getMessage());
        }
    }

    // Getters/Setters
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public Integer getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(Integer quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }

    private void addMsg(String m) {
        FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(m));
    }
}
