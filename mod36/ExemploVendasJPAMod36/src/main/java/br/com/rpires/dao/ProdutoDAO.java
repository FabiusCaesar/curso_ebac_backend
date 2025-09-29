/**
 * 
 */
package br.com.rpires.dao;

import br.com.rpires.dao.generic.GenericJpaDAO;
import br.com.rpires.domain.Produto;

/**
 * @author FabiusCaesar
 * @date 16 de set. de 2025
 */

public class ProdutoDAO extends GenericJpaDAO<Produto, String> implements IProdutoDAO{
	
	 public ProdutoDAO() {
		 // entityClass = Produto.class
		 // campoChave  = "codigo"  → chave natural única usada para consultar/excluir
		 super(Produto.class, "codigo");
	 }
}
