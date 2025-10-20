/**
 * 
 */
package dev.fabiuscaesar.vendasonline.controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dev.fabiuscaesar.vendasonline.domain.Cliente;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;
import dev.fabiuscaesar.vendasonline.service.IClienteService;

/**
 * @author FabiusCaesar
 * @date 14 de out. de 2025
 */

@Named
@ViewScoped
public class ClienteController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private IClienteService clienteService;

    private Cliente cliente;
    private List<Cliente> clientes;
    private boolean update;

    // campos com máscara (tela)
    private String cpfMask;
    private String telMask;

    @PostConstruct
    public void init() {
        cliente = new Cliente();
        try {
            clientes = new ArrayList<>(clienteService.buscarTodos());
        } catch (DAOException e) {
            clientes = new ArrayList<>();
            facesErro("Falha ao carregar clientes: " + msgCausa(e));
        }
        update = false;
    }

    public void add() {
        try {
            cliente.setCpf(toLongOrNull(cpfMask));
            cliente.setTel(toLongOrNull(telMask));

            clienteService.cadastrar(cliente);
            recarregarLista();
            limparFormulario();
            facesInfo("Cliente cadastrado com sucesso!");
        } catch (TipoChaveNaoEncontradaException e) {
            facesErro("Campo inválido: " + e.getMessage());
        } catch (DAOException e) {
            facesErro("Erro ao salvar cliente: " + msgCausa(e));
        } catch (Exception e) {
            facesErro("Falha inesperada: " + msgCausa(e));
        }
    }

    public void edit(Cliente c) {
        this.cliente = c;
        this.cpfMask = (c.getCpf() == null) ? null : c.getCpf().toString();
        this.telMask = (c.getTel() == null) ? null : c.getTel().toString();
        this.update = true;
    }

    public void update() {
        try {
            cliente.setCpf(toLongOrNull(cpfMask));
            cliente.setTel(toLongOrNull(telMask));

            clienteService.alterar(cliente);
            recarregarLista();
            limparFormulario();
            facesInfo("Cliente atualizado com sucesso!");
        } catch (Exception e) {
            facesErro("Erro ao atualizar: " + msgCausa(e));
        }
    }

    public void delete(Cliente c) {
        try {
            clienteService.excluir(c); // contrato do service
            clientes.remove(c);
            facesInfo("Cliente excluído!");
        } catch (Exception e) {
            facesErro("Erro ao excluir: " + msgCausa(e));
        }
    }

    public void cancel() { limparFormulario(); }

    public String voltarTelaInicial() { return "/index.xhtml?faces-redirect=true"; }

    // ===== util =====
    private void recarregarLista() throws DAOException {
        clientes = new ArrayList<>(clienteService.buscarTodos());
    }

    private void limparFormulario() {
        cliente = new Cliente();
        cpfMask = null;
        telMask = null;
        update = false;
    }

    private String onlyDigits(String s) { return (s == null) ? null : s.replaceAll("\\D+", ""); }
    private Long toLongOrNull(String masked) {
        String digits = onlyDigits(masked);
        return (digits == null || digits.isBlank()) ? null : Long.valueOf(digits);
    }

    private void facesInfo(String m) {
        javax.faces.context.FacesContext.getCurrentInstance()
            .addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, m, null));
    }
    private void facesErro(String m) {
        javax.faces.context.FacesContext.getCurrentInstance()
            .addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, m, null));
    }
    private String msgCausa(Throwable t) { return (t.getCause() != null ? t.getCause().getMessage() : t.getMessage()); }

    // ===== getters/setters =====
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente c) { this.cliente = c; }
    public List<Cliente> getClientes() { return clientes; }

    // padrão + compat
    public boolean isUpdate() { return update; }
    public void setUpdate(boolean update) { this.update = update; }
    public Boolean getIsUpdate() { return update; }
    public void setIsUpdate(Boolean b) { this.update = (b != null && b); }

    public String getCpfMask() { return cpfMask; }
    public void setCpfMask(String cpfMask) { this.cpfMask = cpfMask; }
    public String getTelMask() { return telMask; }
    public void setTelMask(String telMask) { this.telMask = telMask; }
}
