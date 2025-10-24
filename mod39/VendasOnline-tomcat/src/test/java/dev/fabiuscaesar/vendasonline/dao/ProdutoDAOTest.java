/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dev.fabiuscaesar.vendasonline.domain.Produto;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.MaisDeUmRegistroException;
import dev.fabiuscaesar.vendasonline.exceptions.TableException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;

/**
 * @author FabiusCaesar
 * @date 11 de out. de 2025
 */

public class ProdutoDAOTest {

    private IProdutoDAO produtoDao;

    private EntityManagerFactory emf;
    private EntityManager em;

    public ProdutoDAOTest() {
        // Construtor vazio como no legado
    }

    @Before
    public void start() {
        // Injeta PU de TESTE (H2, RESOURCE_LOCAL)
        emf = Persistence.createEntityManagerFactory("test");
        em  = emf.createEntityManager();

        produtoDao = new ProdutoDAO();
        ((ProdutoDAO) produtoDao).setEntityManager(em);
    }

    @After
    public void end() throws DAOException {
        // Limpeza: exclusão por ENTIDADE em uma única transação
        Collection<Produto> list = produtoDao.buscarTodos();
        em.getTransaction().begin();
        for (Produto p : list) {
            produtoDao.excluir(p);
        }
        em.getTransaction().commit();

        if (em != null && em.isOpen()) em.close();
        if (emf != null && emf.isOpen()) emf.close();
    }

    private Produto criarProduto(String codigo) throws TipoChaveNaoEncontradaException, DAOException {
        Produto produto = new Produto();
        produto.setCodigo(codigo);
        produto.setDescricao("Produto 1");
        produto.setNome("Produto 1");
        produto.setModelo("Modelo 1"); // preservado do legado
        produto.setValor(BigDecimal.TEN);

        em.getTransaction().begin();
        produtoDao.cadastrar(produto); // ignoramos retorno (pode ser void/entidade/boolean)
        em.getTransaction().commit();

        return produto;
    }

    private void excluir(Produto produto) throws DAOException {
        em.getTransaction().begin();
        this.produtoDao.excluir(produto); // exclusão por ENTIDADE
        em.getTransaction().commit();
    }

    @Test
    public void pesquisar() throws MaisDeUmRegistroException, TableException, DAOException, TipoChaveNaoEncontradaException {
        Produto produto = criarProduto("A1");
        Assert.assertNotNull(produto);

        // Finder por CÓDIGO (não usar consultar(codigo))
        Produto produtoDB = this.produtoDao.buscarPorCodigo(produto.getCodigo());
        Assert.assertNotNull(produtoDB);

        excluir(produtoDB);
    }

    @Test
    public void salvar() throws TipoChaveNaoEncontradaException, DAOException, MaisDeUmRegistroException, TableException {
        Produto produto = criarProduto("A2");
        Assert.assertNotNull(produto);

        Produto produtoDB = this.produtoDao.buscarPorCodigo(produto.getCodigo());
        Assert.assertNotNull(produtoDB);

        excluir(produtoDB);
    }

    @Test
    public void excluir() throws DAOException, TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException {
        Produto produto = criarProduto("A3");
        Assert.assertNotNull(produto);

        Produto produtoDB = this.produtoDao.buscarPorCodigo(produto.getCodigo());
        Assert.assertNotNull(produtoDB);

        excluir(produtoDB);

        Produto produtoBD = this.produtoDao.buscarPorCodigo(produto.getCodigo());
        assertNull(produtoBD);
    }

    @Test
    public void alterarProduto() throws TipoChaveNaoEncontradaException, DAOException, MaisDeUmRegistroException, TableException {
        Produto produto = criarProduto("A4");

        produto.setNome("Rodrigo Pires");
        em.getTransaction().begin();
        produtoDao.alterar(produto); // ignoramos retorno
        em.getTransaction().commit();

        Produto produtoBD = this.produtoDao.buscarPorCodigo(produto.getCodigo());
        assertNotNull(produtoBD);
        Assert.assertEquals("Rodrigo Pires", produtoBD.getNome());

        excluir(produtoBD);
        Produto produtoBD1 = this.produtoDao.buscarPorCodigo(produto.getCodigo());
        assertNull(produtoBD1);
    }

    @Test
    public void buscarTodos() throws DAOException, TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException {
        criarProduto("A5");
        criarProduto("A6");

        Collection<Produto> list = produtoDao.buscarTodos();
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());

        em.getTransaction().begin();
        for (Produto prod : list) {
            produtoDao.excluir(prod);
        }
        em.getTransaction().commit();

        list = produtoDao.buscarTodos();
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }
}
