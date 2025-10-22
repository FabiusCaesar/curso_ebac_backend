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
 * @date 21 de out. de 2025
 */

@Named
@ViewScoped
public class ProdutoController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private IProdutoService produtoService;

    private Produto produto;
    private Collection<Produto> produtos;

    // controla se estamos editando (true) ou cadastrando (false)
    private boolean update;

    // ===== Barra de busca (código do produto) =====
    private String codigoBusca;

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

    // ===== CRUD =====
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
            produtoService.excluir(p);
            produtos.remove(p);
            msgInfo("Produto excluído!");
        } catch (Exception e) {
            msgErro("Erro ao excluir produto: " + causa(e));
        }
    }

    public void cancel() {
        limparFormulario();
    }

    // ===== Busca por código (barra de busca acima do formulário) =====
    public void buscarPorCodigo() {
        try {
            if (codigoBusca == null || codigoBusca.trim().isEmpty()) {
                msgWarn("Informe o código do produto para buscar.");
                return;
            }
            Produto encontrado = produtoService.buscarPorCodigo(codigoBusca.trim());
            if (encontrado != null) {
                this.produto = encontrado;
                this.update  = true;
                msgInfo("Produto carregado para edição.");
            } else {
                msgWarn("Produto não encontrado.");
            }
        } catch (Exception e) {
            msgErro("Erro na busca: " + causa(e));
        }
    }

    // ===== util =====
    private void recarregarLista() throws DAOException {
        produtos = produtoService.buscarTodos();
    }

    private void limparFormulario() {
        produto = new Produto();
        update  = false;
        // se quiser também limpar a barra de busca, descomente:
        // codigoBusca = null;
    }

    private void msgInfo(String m) {
        FacesContext.getCurrentInstance()
            .addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, m, null));
    }

    private void msgWarn(String m) {
        FacesContext.getCurrentInstance()
            .addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_WARN, m, null));
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

    public boolean isUpdate() { return update; }
    public void setUpdate(boolean update) { this.update = update; }
    public Boolean getIsUpdate() { return update; }
    public void setIsUpdate(Boolean b) { this.update = (b != null && b); }

    public String getCodigoBusca() { return codigoBusca; }
    public void setCodigoBusca(String codigoBusca) { this.codigoBusca = codigoBusca; }
}
