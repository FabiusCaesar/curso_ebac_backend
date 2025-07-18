/**
 * 
 */
package dev.fabiuscaesar.services;

import dev.fabiuscaesar.domain.Cliente;

/**
 * @author FabiusCaesar
 * @date 17 de jul. de 2025
 */

public interface IClienteService {

	Boolean salvar(Cliente cliente);

	Cliente buscarPorCPF(Long cpf);

	void excluir(Long cpf);

	void alterar(Cliente cliente);

}
