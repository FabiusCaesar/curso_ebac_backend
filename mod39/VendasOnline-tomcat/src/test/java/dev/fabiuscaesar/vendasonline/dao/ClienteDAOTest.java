/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dev.fabiuscaesar.vendasonline.domain.Cliente;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.MaisDeUmRegistroException;
import dev.fabiuscaesar.vendasonline.exceptions.TableException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;
import dev.fabiuscaesar.vendasonline.testinfra.JpaTestSupport;

/**
 * 
 * @author FabiusCaesar
 * @date 10 de out. de 2025
 */

public class ClienteDAOTest extends JpaTestSupport {

    private IClienteDAO clienteDao;
    private EntityManagerFactory emf;
    private EntityManager em;

    @Before
    public void setUp() {
        // Usa a PU "test" definida em src/test/resources/META-INF/persistence.xml (H2)
        emf = Persistence.createEntityManagerFactory("test");
        em  = emf.createEntityManager();

        clienteDao = new ClienteDAO();
        ((ClienteDAO) clienteDao).setEntityManager(em); // setter do GenericJpaDAO para testes
    }

    @After
    public void end() throws DAOException {
        // Limpa os dados criados nos testes (exclusão por ENTIDADE)
        Collection<Cliente> list = clienteDao.buscarTodos();
        em.getTransaction().begin();
        for (Cliente cli : list) {
            clienteDao.excluir(cli);
        }
        em.getTransaction().commit();

        if (em != null && em.isOpen()) em.close();
        if (emf != null && emf.isOpen()) emf.close();
    }

    @Test
    public void pesquisarCliente() throws MaisDeUmRegistroException, TableException, TipoChaveNaoEncontradaException, DAOException {
        Cliente cliente = novoCliente(12312312312L);

        em.getTransaction().begin();
        clienteDao.cadastrar(cliente); // ignoramos o retorno
        em.getTransaction().commit();

        Cliente clienteConsultado = clienteDao.buscarPorCpf(cliente.getCpf());
        Assert.assertNotNull(clienteConsultado);

        em.getTransaction().begin();
        clienteDao.excluir(clienteConsultado);
        em.getTransaction().commit();
    }

    @Test
    public void salvarCliente() throws TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException, DAOException {
        Cliente cliente = novoCliente(56565656565L);

        em.getTransaction().begin();
        clienteDao.cadastrar(cliente); // ignoramos o retorno
        em.getTransaction().commit();

        Cliente clienteConsultado = clienteDao.buscarPorCpf(cliente.getCpf());
        Assert.assertNotNull(clienteConsultado);

        em.getTransaction().begin();
        clienteDao.excluir(clienteConsultado);
        em.getTransaction().commit();
    }

    @Test
    public void excluirCliente() throws TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException, DAOException {
        Cliente cliente = novoCliente(56565656565L);

        em.getTransaction().begin();
        clienteDao.cadastrar(cliente);
        em.getTransaction().commit();

        Cliente clienteConsultado = clienteDao.buscarPorCpf(cliente.getCpf());
        Assert.assertNotNull(clienteConsultado);

        em.getTransaction().begin();
        clienteDao.excluir(clienteConsultado);
        em.getTransaction().commit();

        clienteConsultado = clienteDao.buscarPorCpf(cliente.getCpf());
        Assert.assertNull(clienteConsultado);
    }

    @Test
    public void alterarCliente() throws TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException, DAOException {
        Cliente cliente = novoCliente(56565656565L);

        em.getTransaction().begin();
        clienteDao.cadastrar(cliente);
        em.getTransaction().commit();

        Cliente clienteConsultado = clienteDao.buscarPorCpf(cliente.getCpf());
        Assert.assertNotNull(clienteConsultado);

        em.getTransaction().begin();
        clienteConsultado.setNome("Rodrigo Pires");
        clienteDao.alterar(clienteConsultado); // ignoramos o retorno
        em.getTransaction().commit();

        Cliente clienteAlterado = clienteDao.buscarPorCpf(cliente.getCpf());
        Assert.assertNotNull(clienteAlterado);
        Assert.assertEquals("Rodrigo Pires", clienteAlterado.getNome());

        em.getTransaction().begin();
        clienteDao.excluir(clienteAlterado);
        em.getTransaction().commit();

        clienteConsultado = clienteDao.buscarPorCpf(cliente.getCpf());
        Assert.assertNull(clienteConsultado);
    }

    @Test
    public void buscarTodos() throws TipoChaveNaoEncontradaException, DAOException, MaisDeUmRegistroException, TableException {
        Cliente cliente = novoCliente(56565656565L);
        Cliente cliente1 = novoCliente(56565656569L);

        em.getTransaction().begin();
        clienteDao.cadastrar(cliente);
        clienteDao.cadastrar(cliente1);
        em.getTransaction().commit();

        Collection<Cliente> list = clienteDao.buscarTodos();
        assertTrue(list != null);
        assertTrue(list.size() == 2);

        em.getTransaction().begin();
        for (Cliente cli : list) {
            clienteDao.excluir(cli);
        }
        em.getTransaction().commit();

        Collection<Cliente> list1 = clienteDao.buscarTodos();
        assertTrue(list1 != null);
        assertTrue(list1.size() == 0);
    }

    // ------- helpers -------
    private Cliente novoCliente(Long cpf) {
        Cliente c = new Cliente();
        c.setCpf(cpf);
        c.setNome("Rodrigo");
        c.setCep("01001000");
        c.setCidade("São Paulo");
        c.setEnd("End");
        c.setEstado("SP");
        c.setNumero(10);
        c.setTel(1199999999L);
        return c;
    }
}

