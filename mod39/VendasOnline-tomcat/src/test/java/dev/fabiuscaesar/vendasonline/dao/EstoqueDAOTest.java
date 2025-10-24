/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao;

import dev.fabiuscaesar.vendasonline.domain.Estoque;
import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.EstoqueInsuficienteException;
import org.junit.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;

/**
 * Testes de EstoqueDAO:
 * - EntityManagerFactory único por classe (performance)
 * - EntityManager por método (isolamento)
 * - Limpeza de dados com JPQL (sem derrubar schema entre métodos)
 * 
 * @author FabiusCaesar
 * @date 12 de out. de 2025
 */

public class EstoqueDAOTest {

    private static EntityManagerFactory EMF;

    private EntityManager em;

    private IProdutoDAO produtoDAO;
    private IEstoqueDAO estoqueDAO;

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

        // Limpa estado (ordem por FK)
        em.getTransaction().begin();
        em.createQuery("delete from Estoque").executeUpdate();
        em.createQuery("delete from Produto").executeUpdate();
        em.getTransaction().commit();

     // Cadastra produto base
        produto = new Produto();
        produto.setCodigo("PE-" + (System.nanoTime() % 1_000_000));
        produto.setNome("Produto Teste");
        produto.setModelo("Modelo Teste");
        produto.setDescricao("Produto para testes de estoque");
        produto.setValor(BigDecimal.TEN);

        em.getTransaction().begin();
        Produto salvo = produtoDAO.cadastrar(produto); // retorna Produto
        em.getTransaction().commit();

        Assert.assertNotNull(salvo);
        Assert.assertNotNull(salvo.getId()); // ou Assert.assertNotNull(produto.getId());
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
    public void deveIncrementar() throws Exception {
        Long produtoId = produto.getId();

        // cria registro com quantidade 0
        em.getTransaction().begin();
        Estoque e0 = estoqueDAO.cadastrar(new Estoque(produto, 0)); // retorna Estoque
        em.getTransaction().commit();
        Assert.assertNotNull(e0);

        Estoque lido0 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido0);
        Assert.assertEquals(Integer.valueOf(0), lido0.getQuantidade());

        // faz o UPDATE em lote (DAO já faz flush/clear)
        em.getTransaction().begin();
        estoqueDAO.incrementar(produtoId, 5);
        em.getTransaction().commit();

        Estoque lido1 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido1);
        Assert.assertEquals(Integer.valueOf(5), lido1.getQuantidade());
    }

    @Test
    public void deveDecrementarComSaldoSuficiente() throws Exception {
        Long produtoId = produto.getId();

        em.getTransaction().begin();
        Estoque e = estoqueDAO.cadastrar(new Estoque(produto, 10));
        em.getTransaction().commit();
        Assert.assertNotNull(e);

        Estoque lido0 = estoqueDAO.consultar(produtoId);
        Assert.assertEquals(Integer.valueOf(10), lido0.getQuantidade());

        em.getTransaction().begin();
        estoqueDAO.decrementarOuFalhar(produtoId, 4);
        em.getTransaction().commit();

        Estoque lido1 = estoqueDAO.consultar(produtoId);
        Assert.assertEquals(Integer.valueOf(6), lido1.getQuantidade());
    }

    @Test(expected = EstoqueInsuficienteException.class)
    public void deveFalharSeSaldoInsuficiente() throws Exception {
        Long produtoId = produto.getId();

        em.getTransaction().begin();
        Estoque e = estoqueDAO.cadastrar(new Estoque(produto, 2));
        em.getTransaction().commit();
        Assert.assertNotNull(e);

        Estoque lido0 = estoqueDAO.consultar(produtoId);
        Assert.assertEquals(Integer.valueOf(2), lido0.getQuantidade());

        em.getTransaction().begin();
        try {
            estoqueDAO.decrementarOuFalhar(produtoId, 3); // deve lançar
            Assert.fail("Era esperado EstoqueInsuficienteException");
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        }
    }

    @Test
    public void incrementarNaoAlteraQuandoQtdNaoPositiva() throws Exception {
        Long produtoId = produto.getId();

        em.getTransaction().begin();
        Estoque e = estoqueDAO.cadastrar(new Estoque(produto, 3));
        em.getTransaction().commit();
        Assert.assertNotNull(e);

        Estoque lido0 = estoqueDAO.consultar(produtoId);
        Assert.assertEquals(Integer.valueOf(3), lido0.getQuantidade());

        em.getTransaction().begin();
        estoqueDAO.incrementar(produtoId, 0);   // no-op
        em.getTransaction().commit();

        em.getTransaction().begin();
        estoqueDAO.incrementar(produtoId, -5);  // no-op
        em.getTransaction().commit();

        Estoque lido2 = estoqueDAO.consultar(produtoId);
        Assert.assertEquals(Integer.valueOf(3), lido2.getQuantidade());
    }

    @Test
    public void decrementarNaoAlteraQuandoQtdNaoPositiva() throws Exception {
        Long produtoId = produto.getId();

        em.getTransaction().begin();
        Estoque e = estoqueDAO.cadastrar(new Estoque(produto, 3));
        em.getTransaction().commit();
        Assert.assertNotNull(e);

        Estoque lido0 = estoqueDAO.consultar(produtoId);
        Assert.assertEquals(Integer.valueOf(3), lido0.getQuantidade());

        em.getTransaction().begin();
        estoqueDAO.decrementarOuFalhar(produtoId, 0);   // no-op
        em.getTransaction().commit();

        em.getTransaction().begin();
        estoqueDAO.decrementarOuFalhar(produtoId, -5);  // no-op
        em.getTransaction().commit();

        Estoque lido2 = estoqueDAO.consultar(produtoId);
        Assert.assertEquals(Integer.valueOf(3), lido2.getQuantidade());
    }

    @Test(expected = DAOException.class)
    public void decrementarFalhaSeNaoExisteEstoque() throws Exception {
        Long produtoId = produto.getId();

        Assert.assertNull(estoqueDAO.consultar(produtoId));

        em.getTransaction().begin();
        try {
            estoqueDAO.decrementarOuFalhar(produtoId, 1); // deve lançar DAOException (inexistente)
            Assert.fail("Era esperado DAOException");
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        }

        Assert.assertNull(estoqueDAO.consultar(produtoId));
    }
}
