/**
 * Teste de DAO de Venda:
 * - EntityManagerFactory único por classe (evita recriar schema/DDL a cada método)
 * - EntityManager por método (isolamento)
 * - Limpeza de dados com JPQL (sem drop do schema entre métodos)
 */
package dev.fabiuscaesar.vendasonline.dao;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.fabiuscaesar.vendasonline.domain.Cliente;
import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.domain.Venda;
import dev.fabiuscaesar.vendasonline.domain.Venda.Status;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;

public class VendaDAOTest {

    private static EntityManagerFactory EMF;

    private EntityManager em;

    private IVendaDAO   vendaDao;
    private IClienteDAO clienteDao;
    private IProdutoDAO produtoDao;

    private Cliente cliente;
    private Produto produtoBase;

    @BeforeClass
    public static void beforeAll() {
        // Sobe o EMF uma única vez para a classe inteira (PU "test" -> H2, RESOURCE_LOCAL)
        EMF = Persistence.createEntityManagerFactory("test");
    }

    @AfterClass
    public static void afterAll() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }

    @Before
    public void setUp() throws Exception {
        // EntityManager novo por método (isolamento sem recriar schema)
        em = EMF.createEntityManager();

        vendaDao   = new VendaDAO();
        clienteDao = new ClienteDAO();
        produtoDao = new ProdutoDAO();

        ((VendaDAO)   vendaDao).setEntityManager(em);
        ((ClienteDAO) clienteDao).setEntityManager(em);
        ((ProdutoDAO) produtoDao).setEntityManager(em);

        // Limpa estado (ordem por FK)
        em.getTransaction().begin();
        em.createQuery("delete from ProdutoQuantidade").executeUpdate();
        em.createQuery("delete from Venda").executeUpdate();
        em.createQuery("delete from Produto").executeUpdate();
        em.createQuery("delete from Cliente").executeUpdate();
        em.getTransaction().commit();

        // Dados base do cenário
        cliente     = cadastrarCliente(12312312312L);
        produtoBase = cadastrarProduto("BASE-10", BigDecimal.TEN);
    }

    @After
    public void tearDown() {
        if (em != null && em.isOpen()) {
            try {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.getTransaction().begin();
                em.createQuery("delete from ProdutoQuantidade").executeUpdate();
                em.createQuery("delete from Venda").executeUpdate();
                em.createQuery("delete from Produto").executeUpdate();
                em.createQuery("delete from Cliente").executeUpdate();
                em.getTransaction().commit();
            } finally {
                em.close();
            }
        }
    }

    // -------- TESTES --------

    @Test
    public void pesquisar() throws Exception {
        // Arrange
        Venda v = criarVenda("V1");

        // Act
        em.getTransaction().begin();
        vendaDao.cadastrar(v); // ignoramos retorno
        em.getTransaction().commit();

        // Assert
        Venda vdb = vendaDao.buscarPorCodigo("V1");
        assertNotNull(vdb);
        assertEquals("V1", vdb.getCodigo());
    }

    @Test
    public void salvar() throws Exception {
        Venda v = criarVenda("V2");

        em.getTransaction().begin();
        vendaDao.cadastrar(v);
        em.getTransaction().commit();

        assertEquals(Status.INICIADA, v.getStatus());
        // 2 itens x 10 = 20
        assertTrue(v.getValorTotal().compareTo(BigDecimal.valueOf(20)) == 0);

        Venda vdb = vendaDao.buscarPorCodigo("V2");
        assertNotNull(vdb);
        assertEquals("V2", vdb.getCodigo());
    }

    @Test
    public void cancelarVenda() throws Exception {
        Venda v = criarVenda("V3");
        em.getTransaction().begin();
        vendaDao.cadastrar(v);
        em.getTransaction().commit();

        // Opera sobre a entidade gerenciada do contexto atual
        Venda vdb = vendaDao.buscarPorCodigo("V3");
        em.getTransaction().begin();
        vendaDao.cancelarVenda(vdb);
        em.getTransaction().commit();

        vdb = vendaDao.buscarPorCodigo("V3");
        assertEquals(Status.CANCELADA, vdb.getStatus());
    }

    @Test
    public void adicionarMaisProdutosDoMesmo() throws Exception {
        Venda v = criarVenda("V4");
        em.getTransaction().begin();
        vendaDao.cadastrar(v);
        em.getTransaction().commit();

        Venda vdb = vendaDao.buscarPorCodigo("V4");
        vdb.adicionarProduto(produtoBase, 1); // total 3 unidades

        assertEquals(3, vdb.getQuantidadeTotalProdutos());
        assertTrue(vdb.getValorTotal().compareTo(BigDecimal.valueOf(30)) == 0);
        assertEquals(Status.INICIADA, vdb.getStatus());
    }

    @Test
    public void adicionarMaisProdutosDiferentes() throws Exception {
        Venda v = criarVenda("V5");
        em.getTransaction().begin();
        vendaDao.cadastrar(v);
        em.getTransaction().commit();

        Produto p50 = cadastrarProduto("P-50", BigDecimal.valueOf(50));

        Venda vdb = vendaDao.buscarPorCodigo("V5");
        vdb.adicionarProduto(p50, 1); // 2x10 + 1x50 = 70

        assertEquals(3, vdb.getQuantidadeTotalProdutos());
        assertTrue(vdb.getValorTotal().compareTo(BigDecimal.valueOf(70)) == 0);
        assertEquals(Status.INICIADA, vdb.getStatus());
    }

    @Test(expected = DAOException.class)
    public void salvarVendaMesmoCodigoExistente() throws Exception {
        // 1ª venda
        Venda v1 = criarVenda("DUP");
        em.getTransaction().begin();
        vendaDao.cadastrar(v1);
        em.getTransaction().commit();

        // 2ª venda (mesmo código) -> deve lançar DAOException
        Venda v2 = criarVenda("DUP");

        em.getTransaction().begin();
        try {
            vendaDao.cadastrar(v2);       // deve propagar DAOException (flush/commit)
            em.getTransaction().commit(); // não deve chegar aqui
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // libera locks se exceção disparar no meio
            }
        }
    }

    @Test
    public void removerProduto() throws Exception {
        Venda v = criarVenda("V6");
        em.getTransaction().begin();
        vendaDao.cadastrar(v);
        em.getTransaction().commit();

        Produto p50 = cadastrarProduto("P-50B", BigDecimal.valueOf(50));

        Venda vdb = vendaDao.buscarPorCodigo("V6");
        vdb.adicionarProduto(p50, 1); // 70
        assertEquals(3, vdb.getQuantidadeTotalProdutos());
        assertTrue(vdb.getValorTotal().compareTo(BigDecimal.valueOf(70)) == 0);

        vdb.removerProduto(p50, 1); // volta para 20
        assertEquals(2, vdb.getQuantidadeTotalProdutos());
        assertTrue(vdb.getValorTotal().compareTo(BigDecimal.valueOf(20)) == 0);
        assertEquals(Status.INICIADA, vdb.getStatus());
    }

    @Test
    public void removerApenasUmProduto() throws Exception {
        Venda v = criarVenda("V7");
        em.getTransaction().begin();
        vendaDao.cadastrar(v);
        em.getTransaction().commit();

        Produto p50 = cadastrarProduto("P-50C", BigDecimal.valueOf(50));

        Venda vdb = vendaDao.buscarPorCodigo("V7");
        vdb.adicionarProduto(p50, 1); // 70

        vdb.removerProduto(p50, 1); // 20
        assertEquals(2, vdb.getQuantidadeTotalProdutos());
        assertTrue(vdb.getValorTotal().compareTo(BigDecimal.valueOf(20)) == 0);
        assertEquals(Status.INICIADA, vdb.getStatus());
    }

    @Test
    public void removerTodosProdutos() throws Exception {
        Venda v = criarVenda("V8");
        em.getTransaction().begin();
        vendaDao.cadastrar(v);
        em.getTransaction().commit();

        Produto p50 = cadastrarProduto("P-50D", BigDecimal.valueOf(50));

        Venda vdb = vendaDao.buscarPorCodigo("V8");
        vdb.adicionarProduto(p50, 1); // 70

        vdb.removerTodosProdutos();
        assertEquals(0, vdb.getQuantidadeTotalProdutos());
        assertTrue(vdb.getValorTotal().compareTo(BigDecimal.ZERO) == 0);
        assertEquals(Status.INICIADA, vdb.getStatus());
    }

    @Test
    public void finalizarVenda() throws Exception {
        Venda v = criarVenda("V9");
        em.getTransaction().begin();
        vendaDao.cadastrar(v);
        em.getTransaction().commit();

        Venda vdb = vendaDao.buscarPorCodigo("V9");
        em.getTransaction().begin();
        vendaDao.finalizarVenda(vdb);
        em.getTransaction().commit();

        vdb = vendaDao.buscarPorCodigo("V9");
        assertEquals(Status.CONCLUIDA, vdb.getStatus());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void tentarAdicionarProdutosVendaFinalizada() throws Exception {
        Venda v = criarVenda("V10");
        em.getTransaction().begin();
        vendaDao.cadastrar(v);
        em.getTransaction().commit();

        Venda vdb = vendaDao.buscarPorCodigo("V10");
        em.getTransaction().begin();
        vendaDao.finalizarVenda(vdb);
        em.getTransaction().commit();

        // Deve lançar UnsupportedOperationException
        vdb.adicionarProduto(this.produtoBase, 1);
    }

    // -------- HELPERS --------

    private Cliente cadastrarCliente(Long cpf) throws TipoChaveNaoEncontradaException, DAOException {
        Cliente c = new Cliente();
        c.setCpf(cpf);
        c.setNome("Rodrigo");
        c.setCep("01001000");
        c.setCidade("São Paulo");
        c.setEnd("End");
        c.setEstado("SP");
        c.setNumero(10);
        c.setTel(1199999999L);

        em.getTransaction().begin();
        clienteDao.cadastrar(c);
        em.getTransaction().commit();
        return c;
    }

    private Produto cadastrarProduto(String codigo, BigDecimal valor)
            throws TipoChaveNaoEncontradaException, DAOException {
        Produto p = new Produto();
        p.setCodigo(codigo);
        p.setDescricao("Produto");
        p.setNome("Produto");
        p.setModelo("Modelo");
        p.setValor(valor);

        em.getTransaction().begin();
        produtoDao.cadastrar(p);
        em.getTransaction().commit();
        return p;
    }

    private Venda criarVenda(String codigo) {
        Venda v = new Venda();
        v.setCodigo(codigo);
        v.setDataVenda(Instant.now());
        v.setCliente(this.cliente);
        v.setStatus(Status.INICIADA);
        v.adicionarProduto(this.produtoBase, 2); // 2 x 10
        return v;
    }
}
