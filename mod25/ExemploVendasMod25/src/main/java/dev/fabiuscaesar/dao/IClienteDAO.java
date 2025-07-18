/**
 * 
 */
package dev.fabiuscaesar.dao;

import dev.fabiuscaesar.domain.Cliente;

/**
 * @author FabiusCaesar
 * @date 18 de jul. de 2025
 */

public interface IClienteDAO {

	Boolean salvar(Cliente cliente);
	
	Cliente buscarPorCPF(Long cpf);

	void excluir(Long cpf);

	void alterar(Cliente cliente);
}
