/**
 * 
 */
package dev.fabiuscaesar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dev.fabiuscaesar.dao.ClienteDaoMock;
import dev.fabiuscaesar.dao.IClienteDAO;
import dev.fabiuscaesar.domain.Cliente;
import dev.fabiuscaesar.services.ClienteService;
import dev.fabiuscaesar.services.IClienteService;

/**
 * @author FabiusCaesar
 * @date 16 de jul. de 2025
 */

public class ClienteServiceTest {
	
	private IClienteService clienteService;
	
	private Cliente cliente;
	
	public ClienteServiceTest() {
		IClienteDAO dao = new ClienteDaoMock();
		clienteService = new ClienteService(dao);
	}
	
	@Before
	public void init() {
		cliente = new Cliente();
		cliente.setCpf(99999999999L);
		cliente.setNome("Fabius");
		cliente.setCidade("Ravenna");
		cliente.setEnd("End");
		cliente.setEstado("Emilia-Romagna");
		cliente.setNumero(01);
		cliente.setTel(999999999L);		
		
		clienteService.salvar(cliente);
	}
	
	@Test
	public void pesquisarCliente() {
		
		Cliente clienteConsultado = clienteService.buscarPorCPF(cliente.getCpf());		
		Assert.assertNotNull(clienteConsultado);
	}
	
	@Test
	public void salvarCliente() {
		Boolean retorno = clienteService.salvar(cliente);
		
		Assert.assertTrue(retorno);
	}
	
	@Test
	public void excluirCliente() {
		clienteService.excluir(cliente.getCpf());
	}
	
	@Test
	public void alterarCliente() {
		cliente.setNome("Fabius Caesar");
		clienteService.alterar(cliente);
		
		Assert.assertEquals("Fabius Caesar", cliente.getNome());
	}
}
