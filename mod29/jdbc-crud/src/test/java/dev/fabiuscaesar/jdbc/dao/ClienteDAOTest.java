package dev.fabiuscaesar.jdbc.dao;

import dev.fabiuscaesar.jdbc.config.ConnectionFactory;
import dev.fabiuscaesar.jdbc.dao.impl.ClienteDAOImpl;
import dev.fabiuscaesar.jdbc.model.Cliente;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author FabiusCaesar
 */
public class ClienteDAOTest {

    private ClienteDAO dao;

    @Before
    public void limparTabelaCliente() throws Exception {
        // zera a tabela antes de cada teste e reinicia o contador do SERIAL
        try (Connection conn = ConnectionFactory.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate("TRUNCATE TABLE cliente RESTART IDENTITY");
        }
        // instancia a implementação que vamos testar
        dao = new ClienteDAOImpl();
    }

    @Test
    public void deveCadastrarEBuscarClientePorId() {
        // arrange
        Cliente novo = new Cliente("Agent Smith", "smith_agent@exemplo.com", "5599999999");

        // act
        dao.cadastrar(novo); // deve preencher novo.id
        Long idGerado = novo.getId();
        Optional<Cliente> buscado = dao.buscarPorId(idGerado);

        // assert
        assertNotNull("id deve ser preenchido após cadastrar", idGerado);
        assertTrue("deve encontrar o cliente recém cadastrado", buscado.isPresent());
        assertEquals("nome deve coincidir", "Agent Smith", buscado.get().getNome());
        assertEquals("email deve coincidir", "smith_agent@exemplo.com", buscado.get().getEmail());
        assertEquals("telefone deve coincidir", "5599999999", buscado.get().getTelefone());

    }

    @Test
    public void deveListarTodosOsClientesEmOrdemDeId() {
        // arrange
        Cliente c1 = new Cliente("Morpheus", "morpheus@matrix.com", "55911111111");
        Cliente c2 = new Cliente("Trinity", "trinity@matrix.com", "55922222222");

        dao.cadastrar(c1);
        dao.cadastrar(c2);

        // act
        java.util.List<Cliente> lista = dao.buscarTodos();

        // assert
        assertNotNull("lista não deve ser nula", lista);
        assertEquals("devem existir 2 registros", 2, lista.size());

        // garantindo a ordem por id crescente (1,2)
        assertEquals("primeiro deve ser o id=1", Long.valueOf(1L), lista.get(0).getId());
        assertEquals("segundo deve ser o id=2", Long.valueOf(2L), lista.get(1).getId());

        // conferindo campos principais também
        assertEquals("Morpheus", lista.get(0).getNome());
        assertEquals("Trinity", lista.get(1).getNome());
    }

    @Test
    public void deveAtualizarDadosDeUmCliente() {
        // arrange
        Cliente original = new Cliente("Thomas A. Anderson", "anderson@matrix.com", "55933333333");
        dao.cadastrar(original); // gera id
        Long id = original.getId();

        // sanity check: buscou o cadastrado
        Optional<Cliente> antes = dao.buscarPorId(id);
        assertTrue("cliente deve existir antes do update", antes.isPresent());
        assertEquals("Thomas A. Anderson", antes.get().getNome());
        assertEquals("anderson@matrix.com", antes.get().getEmail());
        assertEquals("55933333333", antes.get().getTelefone());

        // act - alterando os dados em memória e chamando atualizar
        original.setNome("Neo");
        original.setEmail("neo@matrix.com");
        original.setTelefone("55944444444");

        dao.atualizar(original);

        // assert - ler de volta e verificar as mudanças persistidas
        Optional<Cliente> depois = dao.buscarPorId(id);
        assertTrue("cliente deve existir depois do update", depois.isPresent());
        assertEquals("nome deve ter sido atualizado", "Neo", depois.get().getNome());
        assertEquals("email deve ter sido atualizado", "neo@matrix.com", depois.get().getEmail());
        assertEquals("telefone deve ter sido atualizado", "55944444444", depois.get().getTelefone());
    }

    @Test
    public void deveExcluirClientePorId() {
        // arrange
        Cliente c = new Cliente("Cypher", "cypher@matrix.com", "55955555555");
        dao.cadastrar(c);
        Long id = c.getId();

        // sanity check: deve existir antes da exclusão
        Optional<Cliente> antes = dao.buscarPorId(id);
        assertTrue("cliente deve existir antes da exclusão", antes.isPresent());

        // act
        dao.excluir(id);

        // assert
        Optional<Cliente> depois = dao.buscarPorId(id);
        assertFalse("cliente não deve existir após a exclusão", depois.isPresent());

        // e a lista total deve ficar vazia (já que truncamos no @Before e só inserimos 1 aqui)
        java.util.List<Cliente> lista = dao.buscarTodos();
        assertEquals("lista deve ficar vazia após excluir o único cliente", 0, lista.size());
    }
}
