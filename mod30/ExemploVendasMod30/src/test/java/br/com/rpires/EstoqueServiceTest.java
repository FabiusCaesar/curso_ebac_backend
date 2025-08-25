package br.com.rpires;

import br.com.rpires.dao.EstoqueDAO;
import br.com.rpires.dao.IEstoqueDAO;
import br.com.rpires.dao.IProdutoDAO;
import br.com.rpires.dao.ProdutoDAO;
import br.com.rpires.domain.Produto;
import br.com.rpires.exceptions.*;
import br.com.rpires.services.EstoqueService;
import br.com.rpires.services.IEstoqueService;
import org.junit.*;

import java.math.BigDecimal;

/**
 * @author FabiusCaesar
 */
public class EstoqueServiceTest {

    private IProdutoDAO produtoDAO;
    private IEstoqueDAO estoqueDAO;
    private IEstoqueService estoqueService;

    private Produto produto;

    @Before
    public void setUp() throws Exception {
        produtoDAO = new ProdutoDAO();
        estoqueDAO = new EstoqueDAO();

        // A Service concreta receberá o DAO por injeção simples no próximo passo
        estoqueService = new EstoqueService(estoqueDAO);

        // cria um produto válido
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
    public void tearDown() throws Exception {
        // limpa estoque (se existir)
        try {
            if (produto != null && produto.getId() != null) {
                estoqueDAO.excluir(produto.getId());
            }
        } catch (DAOException ignore) {}

        // exclui produto
        if (produto != null && produto.getCodigo() != null) {
            produtoDAO.excluir(produto.getCodigo());
        }
    }

    @Test
    public void deveCriarRegistroDeEstoqueSeAusenteEInicializarEmZero()
            throws DAOException, TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException {
        Long produtoId = produto.getId();

        // Garante que não existe (idempotente para o teste)
        try { estoqueDAO.excluir(produtoId); } catch (DAOException ignore) {}

        // Act
        estoqueService.criarSeAusente(produtoId);
        Integer quantidade = estoqueService.consultarQuantidade(produtoId);

        // Assert
        Assert.assertNotNull(quantidade);
        Assert.assertEquals(Integer.valueOf(0), quantidade);
    }

    @Test
    public void deveIncrementarEDecrementarComSaldoSuficiente()
            throws DAOException, TipoChaveNaoEncontradaException,
            MaisDeUmRegistroException, TableException,
            EstoqueInsuficienteException {
        Long produtoId = produto.getId();

        estoqueService.criarSeAusente(produtoId);
        Assert.assertEquals(Integer.valueOf(0), estoqueService.consultarQuantidade(produtoId));

        // +10
        estoqueService.incrementar(produtoId, 10);
        Assert.assertEquals(Integer.valueOf(10), estoqueService.consultarQuantidade(produtoId));

        // -3 (saldo suficiente)
        estoqueService.decrementarOuFalhar(produtoId, 3);
        Assert.assertEquals(Integer.valueOf(7), estoqueService.consultarQuantidade(produtoId));
    }

    @Test(expected = EstoqueInsuficienteException.class)
    public void deveFalharAoDecrementarComSaldoInsuficiente() throws Exception {
        Long produtoId = produto.getId();

        estoqueService.criarSeAusente(produtoId);
        estoqueService.incrementar(produtoId, 2);
        Assert.assertEquals(Integer.valueOf(2), estoqueService.consultarQuantidade(produtoId));

        estoqueService.decrementarOuFalhar(produtoId, 5); // pede mais do que tem
    }
}