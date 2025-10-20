/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service;

import dev.fabiuscaesar.vendasonline.dao.IProdutoDAO;
import dev.fabiuscaesar.vendasonline.dao.ProdutoDAO;
import dev.fabiuscaesar.vendasonline.domain.Produto;

import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;

/**
 * Testes de ProdutoService:
 * - EMF único por classe (performance)
 * - EM por método (isolamento)
 * - Limpeza com JPQL (sem recriar schema)
 * - Helpers que escondem checked exceptions do service
 *
 * @author FabiusCaesar
 * @date 12 de out. de 2025
 */

public class ProdutoServiceTest {

    private static EntityManagerFactory EMF;

    private EntityManager em;

    private IProdutoDAO     produtoDAO;
    private IProdutoService produtoService;

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
    public void setUp() {
        // EM por método (isolamento)
        em = EMF.createEntityManager();

        produtoDAO = new ProdutoDAO();
        ((ProdutoDAO) produtoDAO).setEntityManager(em);

        produtoService = new ProdutoService(produtoDAO);

        // Limpa estado
        em.getTransaction().begin();
        em.createQuery("delete from Produto").executeUpdate();
        em.getTransaction().commit();

        // Instancia o produto (não persiste aqui)
        produto = new Produto();
        produto.setCodigo("PX" + (System.nanoTime() % 1_000_000)); // <= 10 chars
        produto.setNome("Produto 1");
        produto.setModelo("Modelo 1");              // campo obrigatório
        produto.setDescricao("Produto 1");
        produto.setValor(BigDecimal.TEN);
    }

    @After
    public void tearDown() {
        try {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.getTransaction().begin();
            em.createQuery("delete from Produto").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception ignore) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Test
    public void pesquisar() {
        // precisa existir no banco para consultar
        em.getTransaction().begin();
        Produto salvo = cadastrarSemChecked(produto);
        em.getTransaction().commit();

        Assert.assertNotNull(salvo);
        Assert.assertNotNull(salvo.getId());

        Produto doBanco = consultarPorIdSemChecked(salvo.getId());
        Assert.assertNotNull(doBanco);
        Assert.assertEquals(salvo.getId(), doBanco.getId());
        Assert.assertEquals(produto.getCodigo(), doBanco.getCodigo());
    }

    @Test
    public void salvar() {
        em.getTransaction().begin();
        Produto salvo = cadastrarSemChecked(produto);
        em.getTransaction().commit();

        Assert.assertNotNull(salvo);
        Assert.assertNotNull(salvo.getId());

        Produto doBanco = consultarPorIdSemChecked(salvo.getId());
        Assert.assertNotNull(doBanco);
        Assert.assertNotNull(doBanco.getId());
    }

    @Test
    public void excluir() {
        // cadastra primeiro
        em.getTransaction().begin();
        Produto salvo = cadastrarSemChecked(produto);
        em.getTransaction().commit();
        Assert.assertNotNull(salvo);

        // exclui passando a ENTIDADE (contrato genérico)
        em.getTransaction().begin();
        excluirSemChecked(salvo);
        em.getTransaction().commit();

        Assert.assertNull(consultarPorIdSemChecked(salvo.getId()));
    }

    @Test
    public void alterarProduto() {
        // cadastra
        em.getTransaction().begin();
        Produto salvo = cadastrarSemChecked(produto);
        em.getTransaction().commit();
        Assert.assertNotNull(salvo);

        // altera e persiste
        produto.setNome("Produto Renomeado");
        em.getTransaction().begin();
        alterarSemChecked(produto);
        em.getTransaction().commit();

        Produto doBanco = consultarPorIdSemChecked(produto.getId());
        Assert.assertNotNull(doBanco);
        Assert.assertEquals("Produto Renomeado", doBanco.getNome());
    }

    // ---------- Helpers: escondem checked exceptions do service ----------

    private Produto cadastrarSemChecked(Produto p) {
        try {
            return produtoService.cadastrar(p);
        } catch (Exception e) { // DAOException, TipoChaveNaoEncontradaException, etc.
            throw new AssertionError("Falha ao cadastrar produto", e);
        }
    }

    private Produto consultarPorIdSemChecked(Long id) {
        try {
            return produtoService.consultar(id);
        } catch (Exception e) { // TableException, DAOException, MaisDeUmRegistroException, etc.
            throw new AssertionError("Falha ao consultar produto id=" + id, e);
        }
    }

    private void alterarSemChecked(Produto p) {
        try {
            produtoService.alterar(p);
        } catch (Exception e) {
            throw new AssertionError("Falha ao alterar produto id=" + p.getId(), e);
        }
    }

    private void excluirSemChecked(Produto p) {
        try {
            produtoService.excluir(p);
        } catch (Exception e) {
            throw new AssertionError("Falha ao excluir produto id=" + p.getId(), e);
        }
    }
}
