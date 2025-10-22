/**
 * 
 */
package dev.fabiuscaesar.vendasonline.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import dev.fabiuscaesar.vendasonline.domain.Cliente;
import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.domain.ProdutoQuantidade;
import dev.fabiuscaesar.vendasonline.domain.Venda;
import dev.fabiuscaesar.vendasonline.service.IClienteService;
import dev.fabiuscaesar.vendasonline.service.IProdutoService;
import dev.fabiuscaesar.vendasonline.service.IVendaService;

/**
 * @author FabiusCaesar
 * @date 15 de out. de 2025
 */

@Named
@ViewScoped
public class VendaController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final ZoneId ZONE = ZoneId.systemDefault();

    @Inject private IVendaService vendaService;
    @Inject private IClienteService clienteService;
    @Inject private IProdutoService produtoService;

    private Venda venda;
    private Collection<Venda> vendas;

    private boolean update;
    private LocalDate dataVenda;
    private Integer quantidadeProduto;
    private Produto produtoSelecionado;

    // ===== NOVO: barra de busca por código =====
    private String codigoBusca;

    @PostConstruct
    public void init() {
        try {
            this.venda = new Venda();
            this.vendas = vendaService.buscarTodos();
            this.update = false;
        } catch (Exception e) {
            addMsgErr("Erro ao listar vendas");
        }
    }

    public void cancel() {
        this.venda = new Venda();
        this.dataVenda = null;
        this.quantidadeProduto = null;
        this.produtoSelecionado = null;
        this.update = false;
    }

    public void delete(Venda v) {
        try {
            vendaService.cancelar(v.getCodigo());
            this.vendas = vendaService.buscarTodos();
            cancel();
            addMsgInfo("Venda cancelada");
        } catch (Exception e) {
            addMsgErr("Erro ao cancelar a venda");
        }
    }

    public void finalizar(Venda v) {
        try {
            vendaService.finalizar(v.getCodigo());
            this.vendas = vendaService.buscarTodos();
            cancel();
            addMsgInfo("Venda finalizada");
        } catch (Exception e) {
            addMsgErr("Erro ao finalizar a venda");
        }
    }

    public void add() {
        try {
            if (this.venda.getCliente() == null) {
                addMsgErr("Selecione um cliente");
                return;
            }
            if (this.venda.getProdutos() == null || this.venda.getProdutos().isEmpty()) {
                addMsgErr("Adicione pelo menos um produto");
                return;
            }
            if (this.venda.getCodigo() == null || this.venda.getCodigo().trim().isEmpty()) {
                this.venda.setCodigo(gerarCodigoVendaUnico());
            }
            if (dataVenda != null) {
                this.venda.setDataVenda(dataVenda.atStartOfDay(ZONE).toInstant());
            }

            vendaService.cadastrar(this.venda);
            this.vendas = vendaService.buscarTodos();
            cancel();
            addMsgInfo("Venda criada com sucesso");
        } catch (Exception e) {
            addMsgErr("Erro ao cadastrar a venda");
        }
    }

    public void update() {
        try {
            if (dataVenda != null) {
                this.venda.setDataVenda(dataVenda.atStartOfDay(ZONE).toInstant());
            }
            vendaService.alterar(this.venda);
            this.vendas = vendaService.buscarTodos();
            cancel();
            addMsgInfo("Venda atualizada com sucesso");
        } catch (Exception e) {
            addMsgErr("Erro ao atualizar a venda");
        }
    }

    public void salvar() {
        if (Boolean.TRUE.equals(getIsUpdate())) update();
        else add();
    }

    public java.util.Date toDate(java.time.Instant i) {
        return i == null ? null : java.util.Date.from(i);
    }

    public void adicionarProduto() {
        if (produtoSelecionado == null || quantidadeProduto == null || quantidadeProduto <= 0) return;
        this.venda.adicionarProduto(produtoSelecionado, quantidadeProduto);
        this.produtoSelecionado = null;
        this.quantidadeProduto = null;
    }

    public void removerProduto() {
        if (produtoSelecionado == null) return;
        Optional<ProdutoQuantidade> op = this.venda.getProdutos().stream()
                .filter(pq -> pq.getProduto().getId().equals(produtoSelecionado.getId()))
                .findFirst();
        op.ifPresent(pq -> this.venda.removerProduto(produtoSelecionado, pq.getQuantidade()));
        this.produtoSelecionado = null;
        this.quantidadeProduto = null;
    }

    public void removerProduto(ProdutoQuantidade pq) {
        if (pq == null) return;
        this.venda.removerProduto(pq.getProduto(), pq.getQuantidade());
    }

    public void edit(Venda v) {
        try {
            Venda carregada = vendaService.buscarPorCodigo(v.getCodigo());
            if (carregada == null) {
                addMsgErr("Venda não encontrada");
                return;
            }
            this.update = true;
            this.venda = carregada;
            this.dataVenda = (this.venda.getDataVenda() == null) ? null
                    : LocalDate.ofInstant(this.venda.getDataVenda(), ZONE);
        } catch (Exception e) {
            addMsgErr("Erro ao editar a venda");
        }
    }

    // ===== NOVO: Busca por código (barra acima da tabela) =====
    public void buscarPorCodigo() {
        try {
            if (codigoBusca == null || codigoBusca.trim().isEmpty()) {
                addMsgWarn("Informe o código da venda para buscar.");
                return;
            }
            Venda encontrada = vendaService.buscarPorCodigo(codigoBusca.trim());
            if (encontrada != null) {
                this.venda = encontrada;
                this.update = true;
                this.dataVenda = (encontrada.getDataVenda() == null) ? null
                        : LocalDate.ofInstant(encontrada.getDataVenda(), ZONE);
                addMsgInfo("Venda carregada para edição.");
            } else {
                addMsgWarn("Venda não encontrada.");
            }
        } catch (Exception e) {
            addMsgErr("Erro na busca.");
        }
    }

    // ===== Autocomplete =====
    public List<Cliente> filtrarClientes(String query) { return this.clienteService.filtrarClientes(query); }
    public List<Produto> filtrarProdutos(String query) {
        String q = (query == null ? "" : query.toLowerCase());
        try {
            return produtoService.buscarTodos().stream()
                .filter(p -> (p.getNome()!=null && p.getNome().toLowerCase().contains(q))
                          || (p.getModelo()!=null && p.getModelo().toLowerCase().contains(q))
                          || (p.getDescricao()!=null && p.getDescricao().toLowerCase().contains(q))
                          || (p.getCodigo()!=null && p.getCodigo().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        } catch (Exception e) {
            addMsgErr("Erro ao filtrar produtos");
            return List.of();
        }
    }

    public String voltarTelaInicial() { return "/index.xhtml?faces-redirect=true"; }

    /* ===== mensagens ===== */
    private void addMsgInfo(String m) {
        FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, m, null));
    }
    private void addMsgWarn(String m) {
        FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_WARN, m, null));
    }
    private void addMsgErr(String m) {
        FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, m, null));
    }

    /* ===== geração de código (único) ===== */
    private String gerarCodigoVendaUnico() {
        for (int i = 0; i < 5; i++) {
            String c = gerarCodigoVenda();
            try {
                Venda existente = vendaService.buscarPorCodigo(c);
                if (existente == null) return c;
            } catch (Exception e) {
                return c;
            }
        }
        return "V-" + System.currentTimeMillis();
    }
    private String gerarCodigoVenda() {
        String data = LocalDate.now(ZONE).format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        int rnd = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "V-" + data + "-" + rnd;
    }

    /* ===== getters/setters ===== */
    public Venda getVenda() { return venda; }
    public void setVenda(Venda venda) { this.venda = venda; }
    public Collection<Venda> getVendas() { return vendas; }

    public boolean isUpdate() { return update; }
    public void setUpdate(boolean update) { this.update = update; }
    public Boolean getIsUpdate() { return update; }
    public void setIsUpdate(Boolean b) { this.update = (b != null && b); }

    public LocalDate getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDate dataVenda) { this.dataVenda = dataVenda; }
    public Integer getQuantidadeProduto() { return quantidadeProduto; }
    public void setQuantidadeProduto(Integer quantidadeProduto) { this.quantidadeProduto = quantidadeProduto; }
    public Produto getProdutoSelecionado() { return produtoSelecionado; }
    public void setProdutoSelecionado(Produto produtoSelecionado) { this.produtoSelecionado = produtoSelecionado; }

    public String getCodigoBusca() { return codigoBusca; }
    public void setCodigoBusca(String codigoBusca) { this.codigoBusca = codigoBusca; }
}