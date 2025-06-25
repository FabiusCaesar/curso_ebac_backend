/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dev.fabiuscaesar.cadastroclienteswing.dao;

import dev.fabiuscaesar.cadastroclienteswing.domain.Cliente;
import java.util.List;

/**
 *
 * @author Fabio
 */
public interface IClienteDao {
    
    Boolean cadastrar(Cliente cliente);
    
    void excluir (String cpf);
    
    Cliente consultar (String cpf);
    
    void alterar(Cliente cliente);
    
    List<Cliente> listar();
}
