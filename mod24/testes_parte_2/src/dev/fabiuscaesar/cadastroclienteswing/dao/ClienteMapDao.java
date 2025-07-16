/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dev.fabiuscaesar.cadastroclienteswing.dao;

import dev.fabiuscaesar.cadastroclienteswing.domain.Cliente;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Fabio
 */
public class ClienteMapDao implements IClienteDao {
    
    // Aqui criamos o "banco de dados" em memória
    private Map<String, Cliente> mapaClientes;
    
    // Construtor que inicializa o mapa e os clientes padrão
    public ClienteMapDao() {
        mapaClientes = new HashMap<>();
        popularClientesIniciais();
    }
    
    private void popularClientesIniciais() {
        mapaClientes.putIfAbsent(
                "01",
                new Cliente(
                        "Nancy Thompson",
                        "01",
                        "(11) 99999-1111",
                        "Elm Street, 1428",
                        "12345-678",
                        "Springwood",
                        "Ohio"
                )
        );

        mapaClientes.putIfAbsent(
                "02",
                new Cliente(
                        "Marty McFly",
                        "02",
                        "(22) 98888-2222",
                        "1640 Riverside Drive",
                        "23456-789",
                        "Hill Valley",
                        "California"
                )
        );

        mapaClientes.putIfAbsent(
                "03",
                new Cliente(
                        "Emmett Brown",
                        "03",
                        "(33) 97777-3333",
                        "1646 Riverside Drive",
                        "34567-890",
                        "Hill Valley",
                        "California"
                )
        );

        mapaClientes.putIfAbsent(
                "04",
                new Cliente(
                        "Indiana Jones",
                        "04",
                        "(44) 96666-4444",
                        "101 Adventure Road",
                        "45678-901",
                        "Princeton",
                        "New Jersey"
                )
        );

        mapaClientes.putIfAbsent(
                "05",
                new Cliente(
                        "Ellen Ripley",
                        "05",
                        "(55) 95555-5555",
                        "Nostromo Spaceship",
                        "56789-012",
                        "Deep Space",
                        "Universe"
                )
        );

        mapaClientes.putIfAbsent(
                "06",
                new Cliente(
                        "Sherlock Holmes",
                        "06",
                        "(66) 94444-6666",
                        "221B Baker Street",
                        "67890-123",
                        "London",
                        "England"
                )
        );
    }

    @Override
    public Boolean cadastrar(Cliente cliente) {
        // putIfAbsent coloca o cliente se a chave (cpf) ainda não existe
        Cliente existente = mapaClientes.putIfAbsent(cliente.getCpf(), cliente);
        return existente == null; // true se não existia antes
    }

    @Override
    public void excluir(String cpf) {
        mapaClientes.remove(cpf); // remove o cliente pelo cpf
    }

    @Override
    public Cliente consultar(String cpf) {
        return mapaClientes.get(cpf); // retorna o cliente pelo cpf (ou null)
    }

    @Override
    public void alterar(Cliente cliente) {
        mapaClientes.replace(cliente.getCpf(), cliente); // substitui o cliente existente
    }

    @Override
    public List<Cliente> listar() {
        return new java.util.ArrayList<>(mapaClientes.values());
    }
    
    
}
