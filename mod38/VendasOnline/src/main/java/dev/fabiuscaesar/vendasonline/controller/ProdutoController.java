/**
 * 
 */
package dev.fabiuscaesar.vendasonline.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.service.IProdutoService;

/**
 * @author FabiusCaesar
 * @date 15 de out. de 2025
 */

@Named
@ViewScoped
public class ProdutoController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private IProdutoService produtoService;

    private Produto produto;
    private Collection<Produto> produtos;

    // Propriedade padrão do projeto
    private boolean update;

    @PostConstruct
    public void init() {
        produto = new Produto();
        try {
            produtos = produtoService.buscarTodos();
        } catch (Exception e) {
            produtos = new ArrayList<>();
            msgErro("Erro ao listar produtos: " + causa(e));
        }
        update = false;
    }

    public void add() {
        try {
            produtoService.cadastrar(produto);
            recarregarLista();
            limparFormulario();
            msgInfo("Produto cadastrado com sucesso!");
        } catch (Exception e) {
            msgErro("Erro ao criar produto: " + causa(e));
        }
    }

    public void update() {
        try {
            produtoService.alterar(produto);
            recarregarLista();
            limparFormulario();
            msgInfo("Produto atualizado com sucesso!");
        } catch (Exception e) {
            msgErro("Erro ao atualizar produto: " + causa(e));
        }
    }

    public void edit(Produto p) {
        this.produto = p;
        this.update = true;
    }

    public void delete(Produto p) {
        try {
            produtoService.excluir(p); // contrato do service: excluir(entidade)
            produtos.remove(p);
            msgInfo("Produto excluído!");
        } catch (Exception e) {
            msgErro("Erro ao excluir produto: " + causa(e));
        }
    }

    public void cancel() {
        limparFormulario();
    }

    public String voltarTelaInicial() {
        return "/index.xhtml?faces-redirect=true";
    }

    // ===== util =====
    private void recarregarLista() throws DAOException {
        produtos = produtoService.buscarTodos();
    }

    private void limparFormulario() {
        produto = new Produto();
        update = false;
    }

    private void msgInfo(String m) {
        FacesContext.getCurrentInstance()
            .addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, m, null));
    }

    private void msgErro(String m) {
        FacesContext.getCurrentInstance()
            .addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, m, null));
    }

    private String causa(Throwable t) {
        return (t.getCause() != null ? t.getCause().getMessage() : t.getMessage());
    }

    // ===== getters/setters =====
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public Collection<Produto> getProdutos() { return produtos; }

    // Propriedade "update" (padrão)
    public boolean isUpdate() { return update; }
    public void setUpdate(boolean update) { this.update = update; }

    // Compatibilidade com EL que use "isUpdate" como nome da propriedade
    public Boolean getIsUpdate() { return update; }
    public void setIsUpdate(Boolean b) { this.update = (b != null && b); }
}
