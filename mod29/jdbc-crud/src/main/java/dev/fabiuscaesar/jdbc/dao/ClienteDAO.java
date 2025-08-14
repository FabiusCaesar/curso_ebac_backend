package dev.fabiuscaesar.jdbc.dao;

import dev.fabiuscaesar.jdbc.model.Cliente;

import java.util.List;
import java.util.Optional;


/**
 * @author FabiusCaesar
 */
public interface ClienteDAO {

    void cadastrar(Cliente cliente);

    Optional<Cliente> buscarPorId(Long id);

    List<Cliente> buscarTodos();

    void atualizar(Cliente cliente);

    void excluir(Long id);
}
