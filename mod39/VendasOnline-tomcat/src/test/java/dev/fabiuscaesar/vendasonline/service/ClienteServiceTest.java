/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service;

import dev.fabiuscaesar.vendasonline.dao.ClienteDAO;
import dev.fabiuscaesar.vendasonline.dao.IClienteDAO;
import dev.fabiuscaesar.vendasonline.domain.Cliente;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.exceptions.TipoChaveNaoEncontradaException;

import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author FabiusCaesar
 * @date 12 de out. de 2025
 */

public class ClienteServiceTest {

    private static EntityManagerFactory EMF;

    private EntityManager em;

    private IClienteDAO     clienteDAO;
    private IClienteService clienteService;

    private Cliente cliente;

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
        em = EMF.createEntityManager();

        clienteDAO = new ClienteDAO();
        ((ClienteDAO) clienteDAO).setEntityManager(em);

        clienteService = new ClienteService(clienteDAO);

        em.getTransaction().begin();
        em.createQuery("delete from Cliente").executeUpdate();
        em.getTransaction().commit();

        cliente = new Cliente();
        cliente.setCpf(12312312312L);
        cliente.setNome("Rodrigo");
        cliente.setCidade("São Paulo");
        cliente.setEnd("End");
        cliente.setEstado("SP");
        cliente.setNumero(10);
        cliente.setTel(1199999999L);
        cliente.setCep("01000-000");
    }

    @After
    public void tearDown() {
        try {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.getTransaction().begin();
            em.createQuery("delete from Cliente").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception ignore) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Test
    public void pesquisarCliente() throws Exception {
        em.getTransaction().begin();
        Cliente salvo = clienteService.cadastrar(cliente); // retorna Cliente
        em.getTransaction().commit();
        Assert.assertNotNull(salvo);
        Assert.assertNotNull(salvo.getId());

        Cliente consultado = clienteService.buscarPorCPF(cliente.getCpf());
        Assert.assertNotNull(consultado);
        Assert.assertEquals("Rodrigo", consultado.getNome());
    }

    @Test
    public void salvarCliente() throws TipoChaveNaoEncontradaException, DAOException {
        em.getTransaction().begin();
        Cliente salvo = clienteService.cadastrar(cliente);
        em.getTransaction().commit();

        Assert.assertNotNull(salvo);
        Assert.assertNotNull(salvo.getId());

        Cliente doBanco = clienteService.buscarPorCPF(cliente.getCpf());
        Assert.assertNotNull(doBanco);
        Assert.assertNotNull(doBanco.getId());
    }

    @Test
    public void excluirCliente() throws Exception {
        em.getTransaction().begin();
        Cliente salvo = clienteService.cadastrar(cliente);
        em.getTransaction().commit();
        Assert.assertNotNull(salvo);

        em.getTransaction().begin();
        clienteService.excluir(salvo); // <-- passa a ENTIDADE, não o CPF
        em.getTransaction().commit();

        Assert.assertNull(clienteService.buscarPorCPF(cliente.getCpf()));
    }

    @Test
    public void alterarCliente() throws Exception {
        em.getTransaction().begin();
        Cliente salvo = clienteService.cadastrar(cliente);
        em.getTransaction().commit();
        Assert.assertNotNull(salvo);

        cliente.setNome("Rodrigo Pires");
        em.getTransaction().begin();
        clienteService.alterar(cliente);
        em.getTransaction().commit();

        Cliente doBanco = clienteService.buscarPorCPF(cliente.getCpf());
        Assert.assertNotNull(doBanco);
        Assert.assertEquals("Rodrigo Pires", doBanco.getNome());
    }
}
