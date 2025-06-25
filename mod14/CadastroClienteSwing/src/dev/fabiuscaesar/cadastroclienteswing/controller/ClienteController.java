/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dev.fabiuscaesar.cadastroclienteswing.controller;

import dev.fabiuscaesar.cadastroclienteswing.dao.ClienteMapDao;
import dev.fabiuscaesar.cadastroclienteswing.dao.IClienteDao;
import dev.fabiuscaesar.cadastroclienteswing.domain.Cliente;
import java.util.List;

/**
 *
 * @author Fabio
 */
public class ClienteController {
    
    private final IClienteDao clienteDao;

    // Construtor que instancia o DAO
    public ClienteController() {
        this.clienteDao = new ClienteMapDao();
    }
    
    
    public Boolean cadastrarCliente(
            String nome,
            String cpf,
            String telefone,
            String endereco,
            String cep,
            String cidade,
            String estado
    ) {
        Cliente cliente = new Cliente(nome, cpf, telefone, endereco, cep, cidade, estado);
        return clienteDao.cadastrar(cliente);
    }
    
    public Cliente consultarCliente(String cpf){
        return clienteDao.consultar(cpf);
    }
    
    public void excluirCliente(String cfp) {
        clienteDao.excluir(cfp);
    }
    
    public void alterarCliente(
            String nome,
            String cpf,
            String telefone,
            String endereco,
            String cep,
            String cidade,
            String estado
    ) {
        Cliente cliente = new Cliente(nome, cpf, telefone, endereco, cep, cidade, estado);
        clienteDao.alterar(cliente);
    }
    
    public List<Cliente> listarClientes() {
        return clienteDao.listar();
    }
}
