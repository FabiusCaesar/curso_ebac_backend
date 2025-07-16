package dev.fabiuscaesar;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author FabiusCaesar
 */
public class MainTest {

    @Test
    public void testarCriarListaDePessoas() {
        List<Pessoa> pessoas = Main.criarListaDePessoas();

        assertNotNull(pessoas);
        assertEquals(5, pessoas.size());

        // Verifica se os nomes esperados estão na lista
        assertTrue(pessoas.stream().anyMatch(p -> p.getNome().equals("Júlio César")));
        assertTrue(pessoas.stream().anyMatch(p -> p.getNome().equals("Cleópatra")));
        assertTrue(pessoas.stream().anyMatch(p -> p.getNome().equals("Marco Antônio")));
        assertTrue(pessoas.stream().anyMatch(p -> p.getNome().equals("Lívia Drusa")));
        assertTrue(pessoas.stream().anyMatch(p -> p.getNome().equals("Augusto")));
    }

    @Test
    public void testFiltrarNomesFemininos() {
        List<Pessoa> pessoas = Main.criarListaDePessoas();
        List<String> mulheres = Main.filtrarMulheres(pessoas);

        assertEquals(2, mulheres.size());
        assertTrue(mulheres.contains("Cleópatra"));
        assertTrue(mulheres.contains("Lívia Drusa"));

        // Garante que nenhum nome masculino está na lista
        assertFalse(mulheres.contains("Júlio César"));
        assertFalse(mulheres.contains("Marco Antônio"));
        assertFalse(mulheres.contains("Augusto"));
    }
}
