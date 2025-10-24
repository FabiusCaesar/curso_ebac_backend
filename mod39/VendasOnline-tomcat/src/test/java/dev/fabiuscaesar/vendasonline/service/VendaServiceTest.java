/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.fabiuscaesar.vendasonline.dao.ClienteDAO;
import dev.fabiuscaesar.vendasonline.dao.EstoqueDAO;
import dev.fabiuscaesar.vendasonline.dao.IClienteDAO;
import dev.fabiuscaesar.vendasonline.dao.IEstoqueDAO;
import dev.fabiuscaesar.vendasonline.dao.IProdutoDAO;
import dev.fabiuscaesar.vendasonline.dao.IVendaDAO;
import dev.fabiuscaesar.vendasonline.dao.ProdutoDAO;
import dev.fabiuscaesar.vendasonline.dao.VendaDAO;
import dev.fabiuscaesar.vendasonline.domain.Cliente;
import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.domain.Venda;

/**
* Teste de integração de VendaService usando PU "test" (H2, RESOURCE_LOCAL).
* - EMF único por classe; EM por método
* - Escritas sempre em transação
* - Limpeza com JPQL (sem recriar schema a cada método)
 *
 * @author Fabius…
 * @date 13 de out. de 2025
 */

public class VendaServiceTest {

   // ---------- EMF/EM ----------
   private static EntityManagerFactory EMF;
   private EntityManager em;

   // ---------- DAOs / Services ----------
   private IClienteDAO  clienteDAO;
   private IProdutoDAO  produtoDAO;
   private IVendaDAO    vendaDAO;
   private IEstoqueDAO  estoqueDAO;

   private IClienteService clienteService;
   private IProdutoService produtoService;
   private IEstoqueService estoqueService;
   private IVendaService   vendaService;

   // ---------- Dados base ----------
   private Cliente cliente;
   private Produto produto;
   private Venda   venda;

   // Interface funcional que permite lançar Exception (ao contrário de Runnable).
   @FunctionalInterface
   interface TxWork { void run() throws Exception; }

   private void inTx(TxWork work) {
       em.getTransaction().begin();
       try {
           work.run();
           em.getTransaction().commit();
       } catch (RuntimeException e) {
           if (em.getTransaction().isActive()) em.getTransaction().rollback();
           throw e;
       } catch (Exception e) {
           if (em.getTransaction().isActive()) em.getTransaction().rollback();
           throw new RuntimeException(e);
       }
   }

   private Venda criarVenda(String codigo) {
       Venda v = new Venda();
       v.setCodigo(codigo);
       v.setCliente(cliente);
       v.setStatus(Venda.Status.INICIADA);
       v.setDataVenda(Instant.now());
       v.adicionarProduto(produto, 3); // um item, qtd 3
       return v;
   }

   @BeforeClass
   public static void beforeAll() {
       EMF = Persistence.createEntityManagerFactory("test");
   }

   @AfterClass
   public static void afterAll() {
       if (EMF != null && EMF.isOpen()) EMF.close();
   }

   @Before
   public void setUp() {
       // 1) EM por método (isolamento)
       em = EMF.createEntityManager();

       // 2) DAOs
       clienteDAO = new ClienteDAO();
       produtoDAO = new ProdutoDAO();
       vendaDAO   = new VendaDAO();
       estoqueDAO = new EstoqueDAO();

       ((ClienteDAO) clienteDAO).setEntityManager(em);
       ((ProdutoDAO) produtoDAO).setEntityManager(em);
       ((VendaDAO)   vendaDAO).setEntityManager(em);
       ((EstoqueDAO) estoqueDAO).setEntityManager(em);

       // 3) Services
       clienteService = new ClienteService(clienteDAO);
       produtoService = new ProdutoService(produtoDAO);
       estoqueService = new EstoqueService(estoqueDAO);
       vendaService   = new VendaService(vendaDAO, estoqueService);

       // 4) Limpa estado (ordem por FK)
       em.getTransaction().begin();
       em.createQuery("delete from ProdutoQuantidade").executeUpdate();
       em.createQuery("delete from Venda").executeUpdate();
       em.createQuery("delete from Estoque").executeUpdate();
       em.createQuery("delete from Produto").executeUpdate();
       em.createQuery("delete from Cliente").executeUpdate();
       em.getTransaction().commit();

       // ---- cliente ----
       cliente = new Cliente();
       long cpfAleatorio = ThreadLocalRandom.current().nextLong(10_000_000_000L, 100_000_000_000L);
       cliente.setCpf(cpfAleatorio);
       cliente.setNome("Fabius");
       cliente.setCep("79000000");
       cliente.setCidade("Campo Grande");
       cliente.setEnd("End");
       cliente.setEstado("MS");
       cliente.setNumero(10);
       cliente.setTel(67555555555L);

       inTx(() -> clienteService.cadastrar(cliente));
       Assert.assertNotNull(buscarClientePorCPF(cliente.getCpf()));

       // ---- produto ---- (código ≤ 10 chars)
       produto = new Produto();
       produto.setCodigo("PE" + (System.nanoTime() % 100_000)); // curto para a coluna
       produto.setDescricao("Produto 1");
       produto.setNome("Produto 1");
       produto.setModelo("Modelo 1");
       produto.setValor(BigDecimal.TEN);

       inTx(() -> produtoService.cadastrar(produto));
       // preferimos consultar por ID (contrato genérico do service)
       assertNotNull(consultarProdutoPorId(produto.getId()));

       // ---- estoque inicial = 10 ----
       inTx(() -> {
           estoqueService.criarSeAusente(produto.getId());
           estoqueService.incrementar(produto.getId(), 10); // estoque = 10
       });

       // ---- venda INICIADA com 1 item (qtd 3) ----
       String codVenda = "V" + (System.currentTimeMillis() % 1_000_000);
       venda = criarVenda(codVenda);

       // Não dependemos do tipo de retorno (boolean ou Venda) — só chamamos:
       inTx(() -> vendaService.cadastrar(venda));

       // Confere se a venda foi persistida (por código, via DAO especializado)
       Venda vendaConsultada = vendaDAO.buscarPorCodigo(venda.getCodigo());
       assertNotNull(vendaConsultada);
       assertEquals(venda.getCodigo(), vendaConsultada.getCodigo());
   }

   @After
   public void tearDown() {
       try {
           if (em.getTransaction().isActive()) em.getTransaction().rollback();
           em.getTransaction().begin();
           em.createQuery("delete from ProdutoQuantidade").executeUpdate();
           em.createQuery("delete from Venda").executeUpdate();
           em.createQuery("delete from Estoque").executeUpdate();
           em.createQuery("delete from Produto").executeUpdate();
           em.createQuery("delete from Cliente").executeUpdate();
           em.getTransaction().commit();
       } catch (Exception ignore) {
           if (em.getTransaction().isActive()) em.getTransaction().rollback();
       } finally {
           if (em != null && em.isOpen()) em.close();
       }
   }

   @Test
   public void deveFinalizarVendaEAbaixarEstoque() {
       inTx(() -> vendaService.finalizar(venda.getCodigo()));

       Venda apos = vendaDAO.buscarPorCodigo(venda.getCodigo());
       assertNotNull(apos);
       assertEquals(Venda.Status.CONCLUIDA, apos.getStatus());

       // Estoque: 10 - 3 = 7
       Integer qtd = consultarQuantidade(produto.getId());
       assertEquals(Integer.valueOf(7), qtd);
   }

   @Test
   public void deveCancelarVendaFinalizadaEDevolverEstoque() {
       // se o domínio permitir cancelamento pós-conclusão, este teste deve passar;
       // caso contrário, adapte para cancelar enquanto INICIADA.
       inTx(() -> vendaService.finalizar(venda.getCodigo()));
       inTx(() -> vendaService.cancelar(venda.getCodigo()));

       // (10 - 3) + 3 = 10
       Integer qtd = consultarQuantidade(produto.getId());
       assertEquals(Integer.valueOf(10), qtd);

       Venda aposCancel = vendaDAO.buscarPorCodigo(venda.getCodigo());
       assertEquals(Venda.Status.CANCELADA, aposCancel.getStatus());
   }

   // ---------- Helpers ----------

   private Produto consultarProdutoPorId(Long id) {
       try {
           return produtoService.consultar(id);
       } catch (Exception e) {
           throw new AssertionError("Falha ao consultar produto id=" + id, e);
       }
   }
   
   private Cliente buscarClientePorCPF(Long cpf) {
	    try {
	        return clienteService.buscarPorCPF(cpf);
	    } catch (Exception e) { // DAOException / TableException / etc.
	        throw new RuntimeException("Falha ao buscar cliente por CPF", e);
	    }
	}

	private Integer consultarQuantidade(Long produtoId) {
	    try {
	        return estoqueService.consultarQuantidade(produtoId);
	    } catch (Exception e) { // DAOException / etc.
	        throw new RuntimeException("Falha ao consultar quantidade em estoque", e);
	    }
	}
}
