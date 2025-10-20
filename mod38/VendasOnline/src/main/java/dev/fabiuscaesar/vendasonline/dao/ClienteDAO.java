/**
 * 
 */
package dev.fabiuscaesar.vendasonline.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import dev.fabiuscaesar.vendasonline.dao.generic.GenericJpaDAO;
import dev.fabiuscaesar.vendasonline.domain.Cliente;

/**
 * @author FabiusCaesar
 * @date 10 de out. de 2025
 */

public class ClienteDAO extends GenericJpaDAO<Cliente, Long> implements IClienteDAO {

    public ClienteDAO() {
        // genérico por ID
        super(Cliente.class);
    }

    @Override
    public List<Cliente> filtrarClientes(String query) {
        TypedQuery<Cliente> tpQuery =
            this.entityManager.createNamedQuery("Cliente.findByNome", this.persistenteClass);
        tpQuery.setParameter("nome", "%" + query + "%");
        return tpQuery.getResultList();
    }

    @Override
    public Cliente buscarPorCpf(Long cpf) {
        try {
            return this.entityManager
                .createQuery("SELECT c FROM Cliente c WHERE c.cpf = :cpf", Cliente.class)
                .setParameter("cpf", cpf)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}

