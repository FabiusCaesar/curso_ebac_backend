package dev.fabiuscaesar;

import dev.fabiuscaesar.cadastroclienteswing.controller.ClienteController;
import dev.fabiuscaesar.cadastroclienteswing.domain.Cliente;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClienteControllerTest {

    private ClienteController controller;

    @Before
    public void setUp() {
        controller = new ClienteController(); // reinicia com os dados fictícios
    }

    @Test
    public void deveConsultarClienteExistente() {
        Cliente cliente = controller.consultarCliente("01");
        assertNotNull(cliente);
        assertEquals("Nancy Thompson", cliente.getNome());
    }

    @Test
    public void deveRetornarNullParaClienteInexistente() {
        Cliente cliente = controller.consultarCliente("999");
        assertNull(cliente);
    }

    @Test
    public void deveExcluirCliente() {
        assertNotNull(controller.consultarCliente("02")); // existe
        controller.excluirCliente("02");
        assertNull(controller.consultarCliente("02")); // excluído
    }

    @Test
    public void deveAlterarTelefoneDoCliente() {
        Cliente clienteAntes = controller.consultarCliente("03");
        assertNotNull(clienteAntes);
        assertEquals("(33) 97777-3333", clienteAntes.getTelefone());

        controller.alterarCliente(
                clienteAntes.getNome(),
                clienteAntes.getCpf(),
                "(99) 99999-9999", // novo telefone
                clienteAntes.getEndereco(),
                clienteAntes.getCep(),
                clienteAntes.getCidade(),
                clienteAntes.getEstado()
        );

        Cliente clienteDepois = controller.consultarCliente("03");
        assertEquals("(99) 99999-9999", clienteDepois.getTelefone());
    }
}
