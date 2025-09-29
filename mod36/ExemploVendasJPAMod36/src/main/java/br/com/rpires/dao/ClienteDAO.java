/**
 * 
 */
package br.com.rpires.dao;

import br.com.rpires.dao.generic.GenericJpaDAO;
import br.com.rpires.domain.Cliente;

/**
 * @author FabiusCaesar
 * @date 15 de set. de 2025
 */

public class ClienteDAO extends GenericJpaDAO<Cliente, Long> implements IClienteDAO {
	
	public ClienteDAO() {
        // entityClass = Cliente.class
        // campoChave  = "cpf"  → CPF será usado como campo único para consultar/excluir
        super(Cliente.class, "cpf");
    }
}
