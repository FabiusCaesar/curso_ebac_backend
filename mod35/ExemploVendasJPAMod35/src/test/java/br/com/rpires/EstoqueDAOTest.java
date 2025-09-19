package br.com.rpires;

import br.com.rpires.dao.EstoqueDAO;
import br.com.rpires.dao.IEstoqueDAO;
import br.com.rpires.dao.IProdutoDAO;
import br.com.rpires.dao.ProdutoDAO;
import br.com.rpires.domain.Estoque;
import br.com.rpires.domain.Produto;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.EstoqueInsuficienteException;
import org.junit.*;

import java.math.BigDecimal;

/**
 * @author FabiusCaesar
 */
public class EstoqueDAOTest {

    private IProdutoDAO produtoDAO;
    private IEstoqueDAO estoqueDAO;

    private Produto produto;

    @Before
    public void setUp() throws Exception {

        produtoDAO = new ProdutoDAO();
        estoqueDAO = new EstoqueDAO();

        produto = new Produto();
        produto.setCodigo("PE" + System.currentTimeMillis() % 100000);
        produto.setNome("Produto Teste");
        produto.setModelo("Modelo Teste"); // campo que você adicionou
        produto.setDescricao("Produto para testes de estoque");
        produto.setValor(BigDecimal.valueOf(10.00));

        produtoDAO.cadastrar(produto);
        Assert.assertNotNull(produto.getId());
    }

    @After
    public void tearDown() {
        // limpa estoque (se existir)
        try {
            if (produto != null && produto.getId() != null) {
                estoqueDAO.excluir(produto.getId());
            }
        } catch (Exception ignore) {}

        // exclui produto
        try {
            if (produto != null && produto.getCodigo() != null) {
                produtoDAO.excluir(produto.getCodigo());
            }
        } catch (Exception ignore) {}
    }

    @Test
    public void deveIncrementar() throws Exception {
        Long produtoId = produto.getId();

        // 1) prepara um registro de estoque com quantidade 0
        Estoque e0 = new Estoque(produtoId, 0);
        Assert.assertTrue(estoqueDAO.cadastrar(e0));

        // 2) confere que iniciou em 0
        Estoque lido0 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido0);
        Assert.assertEquals(Integer.valueOf(0), lido0.getQuantidade());

        // 3) chama o método
        estoqueDAO.incrementar(produto.getId(), 5);

        // 4) confere que virou 5
        Estoque lido1 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido1);
        Assert.assertEquals(Integer.valueOf(5), lido1.getQuantidade());
    }

    @Test
    public void deveDecrementarComSaldoSuficiente() throws Exception {
        Long produtoId = produto.getId();

        // 1) prepara um registro de estoque com quantidade 10
        Estoque e10 = new Estoque(produtoId, 10);
        Assert.assertTrue(estoqueDAO.cadastrar(e10));

        // 2) confere que iniciou em 10
        Estoque lido0 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido0);
        Assert.assertEquals(Integer.valueOf(10), lido0.getQuantidade());

        // 3) chama o método
        estoqueDAO.decrementarOuFalhar(produto.getId(), 4);

        // 4) confere que virou 6
        Estoque lido1 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido1);
        Assert.assertEquals(Integer.valueOf(6), lido1.getQuantidade());
    }

    @Test(expected = EstoqueInsuficienteException.class)
    public void deveFalharSeSaldoInsuficiente() throws Exception {
        Long produtoId = produto.getId();

        // 1) prepara um registro de estoque com quantidade 2
        Estoque e2 = new Estoque(produtoId, 2);
        Assert.assertTrue(estoqueDAO.cadastrar(e2));

        // 2) confere que iniciou em 2
        Estoque lido0 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido0);
        Assert.assertEquals(Integer.valueOf(2), lido0.getQuantidade());

        // 3) chama o método
        estoqueDAO.decrementarOuFalhar(produto.getId(), 3);

        // 4) confere que continuou 2
        Estoque lido1 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido1);
        Assert.assertEquals(Integer.valueOf(2), lido1.getQuantidade());
    }

    @Test
    public void incrementarNaoAlteraQuandoQtdNaoPositiva() throws Exception {
        Long produtoId = produto.getId();

        // 1) prepara um registro de estoque com quantidade 10
        Estoque e3 = new Estoque(produtoId, 3);
        Assert.assertTrue(estoqueDAO.cadastrar(e3));

        // 2) confere que iniciou em 3
        Estoque lido0 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido0);
        Assert.assertEquals(Integer.valueOf(3), lido0.getQuantidade());

        // 3) chama o método e tenta incrementar valor zero
        estoqueDAO.incrementar(produto.getId(), 0);

        // 4) confere que continua 3
        Estoque lido1 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido1);
        Assert.assertEquals(Integer.valueOf(3), lido1.getQuantidade());

        // 5) chama o método e tenta incrementar valor negativo
        estoqueDAO.incrementar(produto.getId(), -5);

        // 6) confere que continua 3
        Estoque lido2 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido2);
        Assert.assertEquals(Integer.valueOf(3), lido2.getQuantidade());
    }

    @Test
    public void decrementarNaoAlteraQuandoQtdNaoPositiva() throws Exception {
        Long produtoId = produto.getId();

        // 1) prepara um registro de estoque com quantidade 10
        Estoque e3 = new Estoque(produtoId, 3);
        Assert.assertTrue(estoqueDAO.cadastrar(e3));

        // 2) confere que iniciou em 3
        Estoque lido0 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido0);
        Assert.assertEquals(Integer.valueOf(3), lido0.getQuantidade());

        // 3) chama o método e tenta incrementar valor zero
        estoqueDAO.decrementarOuFalhar(produto.getId(), 0);

        // 4) confere que continua 3
        Estoque lido1 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido1);
        Assert.assertEquals(Integer.valueOf(3), lido1.getQuantidade());

        // 5) chama o método e tenta incrementar valor negativo
        estoqueDAO.decrementarOuFalhar(produto.getId(), -5);

        // 6) confere que continua 3
        Estoque lido2 = estoqueDAO.consultar(produtoId);
        Assert.assertNotNull(lido2);
        Assert.assertEquals(Integer.valueOf(3), lido2.getQuantidade());
    }

    @Test(expected = DAOException.class)
    public void decrementarFalhaSeNaoExisteEstoque() throws Exception {
        Long produtoId = produto.getId();

        // 1) não abre um registro de estoque para este produto

        // 2) confere que não há registro de estoque para o produto
        Estoque lido0 = estoqueDAO.consultar(produtoId);
        Assert.assertNull(lido0);

        // 3) chama o método
        estoqueDAO.decrementarOuFalhar(produto.getId(), 1);

        // 4) confere que continua inexintente
        Estoque lido1 = estoqueDAO.consultar(produtoId);
        Assert.assertNull(lido1);
    }
}
