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

    // barra de busca (CPF com máscara)
    private String cpfBuscaMask;

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

    // ===== CRUD =====
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
        this.cpfMask = (c.getCpf() == null) ? null : formatCpf(c.getCpf());
        this.telMask = (c.getTel() == null) ? null : formatTel(c.getTel());
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

    // ===== Busca por CPF =====
    public void buscarPorCPF() {
        try {
            Long cpf = toLongOrNull(cpfBuscaMask);
            if (cpf == null) {
                facesWarn("Informe um CPF válido para buscar.");
                return;
            }
            Cliente encontrado = clienteService.buscarPorCPF(cpf);
            if (encontrado != null) {
                this.cliente = encontrado;
                this.update  = true;
                // popular máscaras com os dados do encontrado
                this.cpfMask = formatCpf(encontrado.getCpf());
                this.telMask = formatTel(encontrado.getTel());
                facesInfo("Cliente carregado para edição.");
            } else {
                facesWarn("Cliente não encontrado.");
            }
        } catch (Exception e) {
            facesErro("Erro na busca: " + msgCausa(e));
        }
    }

    // ===== util =====
    private void recarregarLista() throws DAOException {
        clientes = new ArrayList<>(clienteService.buscarTodos());
    }

    private void limparFormulario() {
        cliente  = new Cliente();
        cpfMask  = null;
        telMask  = null;
        update   = false;
        // se preferir, limpe também a busca:
        // cpfBuscaMask = null;
    }

    private String onlyDigits(String s) { return (s == null) ? null : s.replaceAll("\\D+", ""); }
    private Long toLongOrNull(String masked) {
        String digits = onlyDigits(masked);
        return (digits == null || digits.isBlank()) ? null : Long.valueOf(digits);
    }

    // Formatação amigável para exibição em tabela
    public String formatCpf(Long cpf) {
        if (cpf == null) return "";
        String s = String.format("%011d", cpf);
        return s.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
    public String formatTel(Long tel) {
        if (tel == null) return "";
        String s = tel.toString();
        // tenta DDD+9; ajuste simples
        if (s.length() == 11)
            return s.replaceFirst("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        if (s.length() == 10)
            return s.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        return s; // fallback
    }

    private void facesInfo(String m) {
        javax.faces.context.FacesContext.getCurrentInstance()
            .addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_INFO, m, null));
    }
    private void facesWarn(String m) {
        javax.faces.context.FacesContext.getCurrentInstance()
            .addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_WARN, m, null));
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

    public boolean isUpdate() { return update; }
    public void setUpdate(boolean update) { this.update = update; }
    public Boolean getIsUpdate() { return update; }
    public void setIsUpdate(Boolean b) { this.update = (b != null && b); }

    public String getCpfMask() { return cpfMask; }
    public void setCpfMask(String cpfMask) { this.cpfMask = cpfMask; }
    public String getTelMask() { return telMask; }
    public void setTelMask(String telMask) { this.telMask = telMask; }

    public String getCpfBuscaMask() { return cpfBuscaMask; }
    public void setCpfBuscaMask(String cpfBuscaMask) { this.cpfBuscaMask = cpfBuscaMask; }
}
