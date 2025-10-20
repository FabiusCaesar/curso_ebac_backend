/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service;

import dev.fabiuscaesar.vendasonline.dao.EstoqueDAO;
import dev.fabiuscaesar.vendasonline.dao.IEstoqueDAO;
import dev.fabiuscaesar.vendasonline.dao.IProdutoDAO;
import dev.fabiuscaesar.vendasonline.dao.ProdutoDAO;
import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.EstoqueInsuficienteException;

import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;

/**
* Testes do EstoqueService alinhados ao legado:
* - EMF estático por classe; EM por método
* - begin/commit explícitos nas escritas
* - limpeza por JPQL entre métodos
*
* @author Fabius…
* @date 13 de out. de 2025
*/

public class EstoqueServiceTest {

   // ---------- EMF/EM ----------
   private static EntityManagerFactory EMF;
   private EntityManager em;

   // ---------- DAOs / Service ----------
   private IProdutoDAO   produtoDAO;
   private IEstoqueDAO   estoqueDAO;
   private IEstoqueService estoqueService;

   // ---------- Dados base ----------
   private Produto produto;

   @BeforeClass
   public static void beforeAll() {
       EMF = Persistence.createEntityManagerFactory("test");
   }

   @AfterClass
   public static void afterAll() {
       if (EMF != null && EMF.isOpen()) EMF.close();
   }

   @Before
   public void setUp() throws Exception {
       em = EMF.createEntityManager();

       produtoDAO = new ProdutoDAO();
       estoqueDAO = new EstoqueDAO();
       ((ProdutoDAO) produtoDAO).setEntityManager(em);
       ((EstoqueDAO) estoqueDAO).setEntityManager(em);

       estoqueService = new EstoqueService(estoqueDAO);

       // Limpa estado (ordem por FK relevante)
       em.getTransaction().begin();
       em.createQuery("delete from Estoque").executeUpdate();
       em.createQuery("delete from Produto").executeUpdate();
       em.getTransaction().commit();

       // Cria um produto válido (código <= 10 chars)
       produto = new Produto();
       produto.setCodigo("PE" + (System.currentTimeMillis() % 100000));
       produto.setNome("Produto Teste");
       produto.setModelo("Modelo Teste");
       produto.setDescricao("Produto para testes de estoque");
       produto.setValor(BigDecimal.TEN);

       em.getTransaction().begin();
       Produto salvo = produtoDAO.cadastrar(produto);  // retorna Produto
       em.getTransaction().commit();

       Assert.assertNotNull(salvo);
       Assert.assertNotNull(salvo.getId());
   }

   @After
   public void tearDown() {
       try {
           if (em.getTransaction().isActive()) em.getTransaction().rollback();
           em.getTransaction().begin();
           em.createQuery("delete from Estoque").executeUpdate();
           em.createQuery("delete from Produto").executeUpdate();
           em.getTransaction().commit();
       } catch (Exception ignore) {
           if (em.getTransaction().isActive()) em.getTransaction().rollback();
       } finally {
           if (em != null && em.isOpen()) em.close();
       }
   }

   @Test
   public void deveCriarRegistroDeEstoqueSeAusenteEInicializarEmZero() throws Exception {
       Long produtoId = produto.getId();

       // garante ausência (idempotente)
       em.getTransaction().begin();
       em.createQuery("delete from Estoque e where e.produto.id = :pid")
         .setParameter("pid", produtoId)
         .executeUpdate();
       em.getTransaction().commit();

       // cria se ausente
       em.getTransaction().begin();
       estoqueService.criarSeAusente(produtoId);
       em.getTransaction().commit();

       Integer quantidade = consultarQuantidade(produtoId);
       Assert.assertNotNull(quantidade);
       Assert.assertEquals(Integer.valueOf(0), quantidade);
   }

   @Test
   public void deveIncrementarEDecrementarComSaldoSuficiente() throws Exception {
       Long produtoId = produto.getId();

       em.getTransaction().begin();
       estoqueService.criarSeAusente(produtoId);
       em.getTransaction().commit();
       Assert.assertEquals(Integer.valueOf(0), consultarQuantidade(produtoId));

       // +10
       em.getTransaction().begin();
       estoqueService.incrementar(produtoId, 10);
       em.getTransaction().commit();
       Assert.assertEquals(Integer.valueOf(10), consultarQuantidade(produtoId));

       // -3
       em.getTransaction().begin();
       estoqueService.decrementarOuFalhar(produtoId, 3);
       em.getTransaction().commit();
       Assert.assertEquals(Integer.valueOf(7), consultarQuantidade(produtoId));
   }

   @Test(expected = EstoqueInsuficienteException.class)
   public void deveFalharAoDecrementarComSaldoInsuficiente() throws Exception {
       Long produtoId = produto.getId();

       em.getTransaction().begin();
       estoqueService.criarSeAusente(produtoId);
       em.getTransaction().commit();

       em.getTransaction().begin();
       estoqueService.incrementar(produtoId, 2);
       em.getTransaction().commit();
       Assert.assertEquals(Integer.valueOf(2), consultarQuantidade(produtoId));

       // tenta debitar mais do que possui → deve lançar
       em.getTransaction().begin();
       try {
           estoqueService.decrementarOuFalhar(produtoId, 5);
           em.getTransaction().commit(); // não deve chegar aqui
       } finally {
           // se a exception for lançada no meio, garantir rollback
           if (em.getTransaction().isActive()) em.getTransaction().rollback();
       }
   }

   // ---------- Helper de leitura sem "throws" nos testes ----------

   private Integer consultarQuantidade(Long produtoId) {
       try {
           return estoqueService.consultarQuantidade(produtoId);
       } catch (DAOException e) {
           throw new AssertionError("Falha ao consultar quantidade para produtoId=" + produtoId, e);
       }
   }
}
