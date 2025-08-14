package dev.fabiuscaesar.jdbc.dao;

import dev.fabiuscaesar.jdbc.model.Produto;

import java.util.List;
import java.util.Optional;

/**
 * @author FabiusCaesar
 */
public interface ProdutoDAO {

    void cadastrar(Produto produto);

    Optional<Produto> buscarPorId(Long id);

    List<Produto> buscarTodos();

    void atualizar(Produto produto);

    void excluir(Long id);
}
