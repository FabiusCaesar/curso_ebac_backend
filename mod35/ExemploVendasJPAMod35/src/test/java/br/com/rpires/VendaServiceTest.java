package br.com.rpires;

import br.com.rpires.dao.*;
import br.com.rpires.domain.Cliente;
import br.com.rpires.domain.Produto;
import br.com.rpires.domain.Venda;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.services.EstoqueService;
import br.com.rpires.services.IEstoqueService;
import br.com.rpires.services.IVendaService;
import br.com.rpires.services.VendaService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

/**
 * @author FabiusCaesar
 */
public class VendaServiceTest {

    IClienteDAO clienteDAO;
    IProdutoDAO produtoDAO;
    IVendaDAO vendaDAO;
    IEstoqueDAO estoqueDAO;

    IEstoqueService estoqueService;
    IVendaService vendaService;

    Cliente cliente;
    Produto produto;
    Venda venda;

    private Venda criarVenda(String codigo) {
        // TODO:
        // - instanciar Venda
        venda = new Venda();
        // - setar codigo, cliente, status ABERTA, dataVenda (Instant.now())
        venda.setCodigo(codigo);
        venda.setCliente(cliente);
        venda.setStatus(Venda.Status.INICIADA);
        venda.setDataVenda(java.time.Instant.now());
        // - adicionar 1 item: produto + quantidade 3 (como nos outros testes do projeto)
        venda.adicionarProduto(produto, 3);
        // - retornar a venda
        return venda;
    }


    @Before
    public void setUp() throws Exception {

        clienteDAO = new ClienteDAO();
        produtoDAO = new ProdutoDAO();
        vendaDAO = new VendaDAO();
        estoqueDAO = new EstoqueDAO();
        estoqueService = new EstoqueService(estoqueDAO);
        vendaService = new VendaService(vendaDAO, estoqueService);

        cliente = new Cliente();

        // gera um número de 11 dígitos (entre 10000000000 e 99999999999)
        long cpfAleatorio = ThreadLocalRandom.current()
                .nextLong(10000000000L, 100000000000L);

        cliente.setCpf(cpfAleatorio);
        cliente.setNome("Fabius");
        cliente.setCep("79000000");
        cliente.setCidade("Campo Grande");
        cliente.setEnd("End");
        cliente.setEstado("MS");
        cliente.setNumero(10);
        cliente.setTel(67555555555L);

        clienteDAO.cadastrar(cliente);

        Assert.assertNotNull(clienteDAO.consultar(cliente.getCpf()));

        produto = new Produto();

        produto.setCodigo("PE" + (System.currentTimeMillis() % 100000));
        produto.setDescricao("Produto 1");
        produto.setNome("Produto 1");
        produto.setModelo("Modelo 1");
        produto.setValor(BigDecimal.TEN);

        produtoDAO.cadastrar(produto);

        Assert.assertNotNull(produtoDAO.consultar(produto.getCodigo()));

        estoqueService.criarSeAusente(produto.getId());
        estoqueService.incrementar(produto.getId(), 10);

        String codVenda = "V" + (System.currentTimeMillis() % 1_000_000);

        venda = criarVenda(codVenda);
        assertTrue(vendaDAO.cadastrar(venda));
        Venda vendaConsultada = vendaDAO.consultar(venda.getCodigo());
        assertNotNull(vendaConsultada);
        assertEquals(venda.getCodigo(), vendaConsultada.getCodigo());
    }

    @After
    public void tearDown() throws Exception {
        try {
            if (produto != null && produto.getId() != null) {
                estoqueDAO.excluir(produto.getId());
            }
        } catch (DAOException ignore) {}

        try {
            if (produto != null && produto.getCodigo() != null) {
                produtoDAO.excluir(produto.getCodigo());
            }
        } catch (DAOException ignore) {}

        try {
            if (cliente != null && cliente.getCpf() != null) {
                clienteDAO.excluir(cliente.getCpf());
            }
        } catch (DAOException ignore) {}
    }

    @Test
    public void deveFinalizarVendaEAbaixarEstoque() throws Exception {
        // pré-condições respaldadas pelo @Before:
        // - produto com estoque 10
        // - venda "A1" com 1 item de quantidade 3 e status INICIADA

        // Ação: finalizar a venda
        vendaService.finalizar(venda.getCodigo());

        // Verifica status da venda
        Venda apos = vendaDAO.consultar(venda.getCodigo());
        assertNotNull(apos);
        assertEquals(Venda.Status.CONCLUIDA, apos.getStatus());

        // Verifica estoque do produto: 10 - 3 = 7
        Integer qtd = estoqueService.consultarQuantidade(produto.getId());
        assertEquals(Integer.valueOf(7), qtd);
    }

    @Test
    public void deveCancelarVendaFinalizadaEDevolverEstoque() throws Exception {

        // Ação: finalizar a venda
        vendaService.finalizar(venda.getCodigo());

        // Verifica status da venda
        Venda apos = vendaDAO.consultar(venda.getCodigo());
        assertNotNull(apos);
        assertEquals(Venda.Status.CONCLUIDA, apos.getStatus());

        vendaService.cancelar(venda.getCodigo());

        // Verifica estoque do produto: 10 - 3 = 7 => + 3 = 10
        Integer qtd = estoqueService.consultarQuantidade(produto.getId());
        assertEquals(Integer.valueOf(10), qtd);

        // Verifica status da venda após cancelamento
        Venda aposCancel = vendaDAO.consultar(venda.getCodigo());
        assertEquals(Venda.Status.CANCELADA, aposCancel.getStatus());
    }
}
