/**
 * 
 */
package dev.fabiuscaesar.services;

import dev.fabiuscaesar.dao.IClienteDAO;
import dev.fabiuscaesar.domain.Cliente;

/**
 * @author FabiusCaesar
 * @date 17 de jul. de 2025
 */

public class ClienteService implements IClienteService {
	
	private IClienteDAO clienteDAO;
	
	public ClienteService(IClienteDAO clienteDAO) {
		this.clienteDAO = clienteDAO;
	}

	@Override
	public Boolean salvar(Cliente cliente) {
		return clienteDAO.salvar(cliente);

	}

	@Override
	public Cliente buscarPorCPF(Long cpf) {
		return clienteDAO.buscarPorCPF(cpf);
	}

	@Override
	public void excluir(Long cpf) {
		clienteDAO.excluir(cpf);
		
	}

	@Override
	public void alterar(Cliente cliente) {
		clienteDAO.alterar(cliente);
		
	}


}
