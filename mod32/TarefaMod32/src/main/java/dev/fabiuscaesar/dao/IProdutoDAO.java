/**
 * 
 */
package dev.fabiuscaesar.dao;

import java.util.List;
import java.util.Optional;

import dev.fabiuscaesar.domain.Produto;

/**
 * @author FabiusCaesar
 * @date 27 de ago. de 2025
 */

public interface IProdutoDAO {
	
	Produto cadastrar(Produto produto);

	Optional<Produto> buscarPorCodigo(String codigo);

	boolean excluir(Long id);

	Optional<Produto> buscarPorId(Long id);

	Produto atualizar(Produto produto);

	List<Produto> listarTodos();

}
