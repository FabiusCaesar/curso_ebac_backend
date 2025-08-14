package dev.fabiuscaesar.jdbc.dao;

import dev.fabiuscaesar.jdbc.config.ConnectionFactory;
import dev.fabiuscaesar.jdbc.dao.impl.ProdutoDAOImpl;
import dev.fabiuscaesar.jdbc.model.Produto;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author FabiusCaesar
 */
public class ProdutoDAOTest {

    private ProdutoDAO dao;

    @Before
    public void limparTabelaProduto() throws Exception {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement st = conn.createStatement()) {
            // Reinicia IDs para previsibilidade nos testes
            st.executeUpdate("TRUNCATE TABLE produto RESTART IDENTITY");
        }
        dao = new ProdutoDAOImpl();
    }

    @Test
    public void deveCadastrarEBuscarProdutoPorId() {
        // arrange
        Produto novo = new Produto(
                "Óculos do Neo",
                "Óculos escuros retangulares",
                new BigDecimal("899.90")
        );

        // act
        dao.cadastrar(novo);
        Long idGerado = novo.getId();
        Optional<Produto> buscado = dao.buscarPorId(idGerado);

        // assert
        assertNotNull("id deve ser preenchido após cadastrar", idGerado);
        assertTrue("deve encontrar o produto recém cadastrado", buscado.isPresent());
        assertEquals("nome deve coincidir", "Óculos do Neo", buscado.get().getNome());
        assertEquals("descrição deve coincidir", "Óculos escuros retangulares", buscado.get().getDescricao());
        assertEquals("preço deve coincidir", 0, buscado.get().getPreco().compareTo(new BigDecimal("899.90")));

    }

    @Test
    public void deveListarTodosOsProdutosEmOrdemDeId() {
        // arrange
        Produto p1 = new Produto(
                "Óculos da Trinity",
                "Óculos escuros ovais",
                new BigDecimal("849.50")
        );

        Produto p2 = new Produto(
                "Casaco Longo do Morpheus",
                "Casaco de couro preto",
                new BigDecimal("1999.00")
        );

        dao.cadastrar(p1);
        dao.cadastrar(p2);

        // act
        java.util.List<Produto> lista = dao.buscarTodos();

        // assert
        assertNotNull("lista não deve ser nula", lista);
        assertEquals("devem existir 2 registros", 2, lista.size());

        // garantindo a ordem por id crescente (1,2)
        assertEquals("primeiro deve ser o id=1", Long.valueOf(1L), lista.get(0).getId());
        assertEquals("segundo deve ser o id=2", Long.valueOf(2L), lista.get(1).getId());

        // conferindo campos principais também
        assertEquals("Óculos da Trinity", lista.get(0).getNome());
        assertEquals("Casaco Longo do Morpheus", lista.get(1).getNome());
    }

    @Test
    public void deveAtualizarDadosDeUmProduto() {
        // arrange
        Produto original = new Produto(
                "Nokia 8110",
                "Celular deslizante usado para comunicação fora da Matrix",
                new BigDecimal("599.90")
        );

        dao.cadastrar(original); // gera id
        Long id = original.getId();

        // sanity check: buscou o cadastrado
        Optional<Produto> antes = dao.buscarPorId(id);
        assertTrue("produto deve existir antes do update", antes.isPresent());
        assertEquals("Nokia 8110", antes.get().getNome());
        assertEquals("Celular deslizante usado para comunicação fora da Matrix", antes.get().getDescricao());
        assertEquals(0, antes.get().getPreco().compareTo(new BigDecimal("599.90")));

        // act - atualizamos: descrição e preço
        original.setDescricao("Nokia 8110 (banana phone) usado na trilogia");
        original.setPreco(new BigDecimal("649.00"));

        dao.atualizar(original);

        // assert - ler de volta e verificar as mudanças persistidas
        Optional<Produto> depois = dao.buscarPorId(id);
        assertTrue("produto deve existir depois do update", depois.isPresent());
        assertEquals("Nokia 8110", depois.get().getNome()); // nome mantido
        assertEquals("descrição deve ter sido atualizada", "Nokia 8110 (banana phone) usado na trilogia", depois.get().getDescricao());
        assertEquals("preço deve ter sido atualizado", 0, depois.get().getPreco().compareTo(new BigDecimal("649.00")));
    }

    @Test
    public void deveExcluirProdutoPorId() {
        // arrange
        Produto p = new Produto(
                "Beretta 92FS",
                "Pistola usada em diversas cenas de ação",
                new BigDecimal("2999.00")
        );
        dao.cadastrar(p);
        Long id = p.getId();

        // sanity check: deve existir antes da exclusão
        Optional<Produto> antes = dao.buscarPorId(id);
        assertTrue("produto deve existir antes da exclusão", antes.isPresent());

        // act
        dao.excluir(id);

        // assert
        Optional<Produto> depois = dao.buscarPorId(id);
        assertFalse("produto não deve existir após a exclusão", depois.isPresent());

        // e a lista total deve ficar vazia (já que truncamos no @Before e só inserimos 1 aqui)
        java.util.List<Produto> lista = dao.buscarTodos();
        assertEquals("lista deve ficar vazia após excluir o único produto", 0, lista.size());
    }
}
