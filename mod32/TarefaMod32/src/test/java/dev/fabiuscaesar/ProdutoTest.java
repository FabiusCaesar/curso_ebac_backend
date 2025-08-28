/**
 * 
 */
package dev.fabiuscaesar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import dev.fabiuscaesar.dao.IProdutoDAO;
import dev.fabiuscaesar.dao.ProdutoDAO;
import dev.fabiuscaesar.domain.Produto;
import dev.fabiuscaesar.infra.JPAUtil;

/**
 * @author FabiusCaesar
 * @date 27 de ago. de 2025
 */

public class ProdutoTest {

	private IProdutoDAO produtoDao;
	
	private List<Long> idsCriados = new ArrayList<>();
	
	@Before
	public void setUp() {
		 // inicializa o DAO
        produtoDao = new ProdutoDAO();

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
        	em.getTransaction().begin();
        	// opção 1: limpar via JPQL (não reinicia a sequência):
        	em.createQuery("DELETE FROM Produto").executeUpdate();
        	
        	// opção 2: se quiser resetar o id também (usa query nativa no Postgres):
        	em.createNativeQuery("ALTER SEQUENCE seq_produto RESTART WITH 1").executeUpdate();
        	
        	em.getTransaction().commit();        	
        } finally {
        	if (em.isOpen()) em.close();        	
        }        
	}
	
	@After
	public void tearDown() {
	    for (Long id : idsCriados) {
	        try {
	            produtoDao.excluir(id); // se já tiver sido excluído no teste, só retornará false
	        } catch (Exception e) {
	            // não deixe o teardown mascarar falhas do teste principal
	            System.err.println("Falha ao excluir id " + id + ": " + e.getMessage());
	        }
	    }
	    idsCriados.clear();
	}

	@Test
	public void deveCadastrarProdutoComIdGerado() {
		
		// Arrange
		Produto produto = new Produto();		
		produto.setCodigo("P1");
		produto.setNome("Faca Ka Bar 1217 USMC");
		produto.setPreco(new BigDecimal("1349.99"));
				
		// Act
		produto = produtoDao.cadastrar(produto);
		idsCriados.add(produto.getId()); // registra para o @After
		
		// Assert
		assertNotNull(produto);
		assertNotNull(produto.getId());
	}
	
	@Test
	public void deveConsultarProdutoPorCodigo() {
		
		// Arrange
		Produto produto = new Produto();		
		produto.setCodigo("P1");
		produto.setNome("Faca Ka Bar 1217 USMC");
		produto.setPreco(new BigDecimal("1349.99"));
		
		produto = produtoDao.cadastrar(produto);
		idsCriados.add(produto.getId()); // registra para o @After
		
		// Act
		Optional<Produto> opt = produtoDao.buscarPorCodigo(produto.getCodigo());
		assertTrue(opt.isPresent());
		
		// Assert
		Produto encontrado = opt.get();
		assertEquals(produto.getId(), encontrado.getId());
		assertEquals(produto.getCodigo(), encontrado.getCodigo());
		assertEquals(produto.getNome(), encontrado.getNome());
		assertEquals(produto.getPreco(), encontrado.getPreco());
	}
	
	@Test
	public void deveRetornarVazioQuandoBuscarPorProdutoInexistente() {
		// Arrange: base já está limpa pelo @Before
		
	    // Act
	    var opt = produtoDao.buscarPorCodigo("NAO_EXISTE");

	    assertTrue(opt.isEmpty());
	}
	
	@Test
	public void deveExcluirProdutoQuandoProdutoExiste() {
		
		// Arrange
		Produto produto = new Produto();		
		produto.setCodigo("P1");
		produto.setNome("Faca Ka Bar 1217 USMC");
		produto.setPreco(new BigDecimal("1349.99"));		
		produto = produtoDao.cadastrar(produto);
		
		assertNotNull(produto.getId());
		
		// Act
		boolean removido = produtoDao.excluir(produto.getId());
		
		// Assert
		assertTrue(removido);
		// confirma no banco que sumiu
		assertTrue(produtoDao.buscarPorCodigo("P1").isEmpty());
	}
	
	@Test
	public void deveRetornarFalseAoExcluirIdInexistente() {
		
	    boolean removido = produtoDao.excluir(999L);
	    
	    assertFalse(removido);
	    
	}
	
	@Test
	public void deveBuscarProdutoPorIdQuandoExiste() {

		// Arrange
		Produto produto = new Produto();		
		produto.setCodigo("P1");
		produto.setNome("Faca Ka Bar 1217 USMC");
		produto.setPreco(new BigDecimal("1349.99"));		
		produto = produtoDao.cadastrar(produto);
				
		// Act
		var opt = produtoDao.buscarPorId(produto.getId());
		
		// Assert
		assertTrue(opt.isPresent());
		var encontrado = opt.get();
		assertEquals(produto.getId(), encontrado.getId());
		assertEquals(produto.getCodigo(), encontrado.getCodigo());
		assertEquals(produto.getNome(), encontrado.getNome());
		assertEquals(produto.getPreco(), encontrado.getPreco());
	}
	
	@Test
	public void deveRetornarVazioAoBuscarPorIdInexistente() {
		
	    var opt = produtoDao.buscarPorId(999L);
	    
	    assertTrue(opt.isEmpty());
	}
	
	@Test
	public void deveAtualizarProdutoQuandoDadosMudam() {
		
		// Arrange
		Produto produto = new Produto();		
		produto.setCodigo("P1");
		produto.setNome("Faca Ka Bar 1217 USMC");
		produto.setPreco(new BigDecimal("1349.99"));
				
		produto = produtoDao.cadastrar(produto);
		idsCriados.add(produto.getId()); // registra para o @After
		
		// Act
		produto.setCodigo("P1");
		produto.setNome("Faca Ka Bar BK2");
		produto.setPreco(new BigDecimal("1399.99"));
				
		produto = produtoDao.atualizar(produto);
		
		var opt = produtoDao.buscarPorId(produto.getId());
				
		// Assert
		assertTrue(opt.isPresent());
		var encontrado = opt.get();
		assertEquals(produto.getId(), encontrado.getId());
		assertEquals(produto.getCodigo(), encontrado.getCodigo());
		assertEquals(produto.getNome(), encontrado.getNome());
		assertEquals(produto.getPreco(), encontrado.getPreco());
	}
	
	@Test
	public void deveListarTodosQuandoBaseVazia() {
		
		List<Produto> lista = produtoDao.listarTodos();
		
		assertNotNull(lista);
		assertTrue(lista.isEmpty());
	}
	
	@Test
	public void deveListarTodosQuandoExistemRegistros() {
		
		// Arrange - produto 1
		Produto produto1 = new Produto();		
		produto1.setCodigo("P1");
		produto1.setNome("Faca Ka Bar 1217 USMC");
		produto1.setPreco(new BigDecimal("1349.99"));
				
		produto1 = produtoDao.cadastrar(produto1);
		idsCriados.add(produto1.getId()); // registra para o @After
		
		// Arrange - produto 2
		Produto produto2 = new Produto();
		produto2.setCodigo("P2");
		produto2.setNome("Faca Ontario Pilot Air Force Survival");
		produto2.setPreco(new BigDecimal("999.99"));
				
		produto2 = produtoDao.cadastrar(produto2);
		idsCriados.add(produto2.getId()); // registra para o @After
		
		// Act
		List<Produto> lista = produtoDao.listarTodos();
		
		// Assert
		assertNotNull(lista);
		assertFalse(lista.isEmpty());
		assertEquals(2, lista.size());
		
		assertEquals(produto1.getId(), lista.get(0).getId());
		assertEquals(produto2.getId(), lista.get(1).getId());
		
		assertEquals("P1", lista.get(0).getCodigo());
		assertEquals("P2", lista.get(1).getCodigo());
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		JPAUtil.shutDown();
	}
}
