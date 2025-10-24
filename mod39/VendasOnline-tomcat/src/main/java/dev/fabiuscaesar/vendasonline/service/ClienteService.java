/**
 * 
 */
package dev.fabiuscaesar.vendasonline.service;


import dev.fabiuscaesar.vendasonline.config.Transactional;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dev.fabiuscaesar.vendasonline.dao.IClienteDAO;
import dev.fabiuscaesar.vendasonline.domain.Cliente;
import dev.fabiuscaesar.vendasonline.exceptions.DAOException;
import dev.fabiuscaesar.vendasonline.service.generic.GenericService;

/**
 * @author FabiusCaesar
 * @date 12 de out. de 2025
 */

@ApplicationScoped
@Transactional
public class ClienteService extends GenericService<Cliente, Long> implements IClienteService {

    private IClienteDAO clienteDAO;

    @Inject
    public ClienteService(IClienteDAO clienteDAO) {
        super(clienteDAO);
        this.clienteDAO = clienteDAO;
    }

    @Override
    public Cliente buscarPorCPF(Long cpf) throws DAOException {
        return clienteDAO.buscarPorCpf(cpf);
    }

    @Override
    public List<Cliente> filtrarClientes(String query) {
        return clienteDAO.filtrarClientes(query);
    }
}
