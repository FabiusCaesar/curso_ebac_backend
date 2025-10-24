/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao;

import java.util.List;

import dev.fabiuscaesar.vendasonline.dao.generic.IGenericDAO;
import dev.fabiuscaesar.vendasonline.domain.Cliente;

/**
 * @author FabiusCaesar
 * @date 10 de out. de 2025
 */

public interface IClienteDAO extends IGenericDAO<Cliente, Long> {

    List<Cliente> filtrarClientes(String query);

    // novo: finder explícito por CPF
    Cliente buscarPorCpf(Long cpf);
}
