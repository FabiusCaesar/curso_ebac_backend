/**
 * 
 */
package dev.fabiuscaesar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dev.fabiuscaesar.dao.ClienteDAO;
import dev.fabiuscaesar.dao.ClienteDaoMock;
import dev.fabiuscaesar.dao.IClienteDAO;
import dev.fabiuscaesar.domain.Cliente;

/**
 * @author FabiusCaesar
 * @date 18 de jul. de 2025
 */
public class ClienteDAOTest {
	
	private IClienteDAO clienteDao;
	
	private Cliente cliente;
	
	public ClienteDAOTest() {
		clienteDao = new ClienteDaoMock();
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
		
		clienteDao.salvar(cliente);
	}
	
	@Test
	public void pesquisarCliente() {
		Cliente clienteConsultado = clienteDao.buscarPorCPF(cliente.getCpf());
		
		Assert.assertNotNull(clienteConsultado);
	}
	
	@Test
	public void salvarCliente() {
		Boolean retorno = clienteDao.salvar(cliente);
		
		Assert.assertTrue(retorno);
	}
	
	@Test
	public void excluirCliente() {
		clienteDao.excluir(cliente.getCpf());
	}
	
	@Test
	public void alterarCliente() {
		cliente.setNome("Fabius Caesar");
		clienteDao.alterar(cliente);
		
		Assert.assertEquals("Fabius Caesar", cliente.getNome());;
	}
}
