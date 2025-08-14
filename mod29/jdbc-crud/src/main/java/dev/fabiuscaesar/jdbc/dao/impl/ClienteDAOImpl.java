package dev.fabiuscaesar.jdbc.dao.impl;

import dev.fabiuscaesar.jdbc.config.ConnectionFactory;
import dev.fabiuscaesar.jdbc.dao.ClienteDAO;
import dev.fabiuscaesar.jdbc.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author FabiusCaesar
 */
public class ClienteDAOImpl implements ClienteDAO {


    @Override
    public void cadastrar(Cliente cliente) {
        final String sql = "INSERT INTO cliente (nome, email, telefone) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1) Preenche os placeholders ? com os valores do objeto
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEmail());
            ps.setString(3, cliente.getTelefone());

            // 2) Executa o INSERT
            ps.executeUpdate();

            // 3) Recupera a chave gerada (id SERIAL) e seta de volta no objeto
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar cliente", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        final String sql = "SELECT id, nome, email, telefone FROM cliente WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1) Passa o parâmetro da cláusula WHERE
            ps.setLong(1, id);

            // 2) Executa o SELECT
            try (ResultSet rs = ps.executeQuery()) {

                // 3) Se encontrou, mapeia as colunas para o POJO
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setId(rs.getLong("id"));
                    c.setNome(rs.getString("nome"));
                    c.setEmail(rs.getString("email"));
                    c.setTelefone(rs.getString("telefone"));

                    // 4) Retorna valor presente
                    return Optional.of(c);
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por id", e);
        }
    }

    @Override
    public List<Cliente> buscarTodos() {
        final String sql = "SELECT id, nome, email, telefone FROM cliente ORDER BY id";

        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getLong("id"));
                c.setNome(rs.getString("nome"));
                c.setEmail(rs.getString("email"));
                c.setTelefone(rs.getString("telefone"));
                clientes.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os clientes", e);
        }
        return clientes;
    }

    @Override
    public void atualizar(Cliente cliente) {
        final String sql = "UPDATE cliente SET nome = ?, email = ?, telefone = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1) Envia os novos valores para o SQL, na ordem exata dos ?
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEmail());
            ps.setString(3, cliente.getTelefone());
            ps.setLong(4, cliente.getId());

            // 2) Executa o UPDATE
            int linhasAfetadas = ps.executeUpdate();

            // 3) Caso nennhuma linha seja afetada, o id não existe
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Nenhum cliente atualizado: id " + cliente.getId() + " não encontrado");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente", e);
        }
    }

    @Override
    public void excluir(Long id) {
        final String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1) Passa o id do registro que queremos excluir
            ps.setLong(1, id);

            // 2) Executa o DELETE e confere quantas linhas foram afetadas
            int linhasAfetadas = ps.executeUpdate();

            // 3) Se não afetou nenhuma linha, o id não existe na tabela
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Nenhum cliente excluído: id " + id + " não encontrado");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir cliente por id", e);
        }
    }
}
