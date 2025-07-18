/**
 * 
 */
package dev.fabiuscaesar.dao;

import dev.fabiuscaesar.domain.Cliente;

/**
 * @author FabiusCaesar
 * @date 18 de jul. de 2025
 */

public class ClienteDaoMock implements IClienteDAO {

	@Override
	public Boolean salvar(Cliente cliente) {
		// TODO Auto-generated method stub
		return true;

	}

	@Override
	public Cliente buscarPorCPF(Long cpf) {
		Cliente cliente = new Cliente();
		cliente.setCpf(cpf);
		return cliente;
	}

	@Override
	public void excluir(Long cpf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alterar(Cliente cliente) {
		// TODO Auto-generated method stub
		
	}

}
