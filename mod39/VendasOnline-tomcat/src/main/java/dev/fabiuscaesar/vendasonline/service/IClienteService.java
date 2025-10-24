/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service;

import java.util.List;

import dev.fabiuscaesar.vendasonline.domain.Cliente;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.service.generic.IGenericService;

/**
 * @author FabiusCaesar
 * @date 12 de out. de 2025
 */

public interface IClienteService extends IGenericService<Cliente, Long> {
	
    Cliente buscarPorCPF(Long cpf) throws DAOException;
    
    List<Cliente> filtrarClientes(String query);
}
