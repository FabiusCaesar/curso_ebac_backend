package dev.fabiuscaesar.jdbc.dao.impl;

import dev.fabiuscaesar.jdbc.config.ConnectionFactory;
import dev.fabiuscaesar.jdbc.dao.ProdutoDAO;
import dev.fabiuscaesar.jdbc.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author FabiusCaesar
 */
public class ProdutoDAOImpl implements ProdutoDAO {

    @Override
    public void cadastrar(Produto produto) {
        final String sql = "INSERT INTO produto (nome, descricao, preco) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1) Preenche os placeholders ? com os valores do objeto
            ps.setString(1, produto.getNome());
            ps.setString(2, produto.getDescricao());
            ps.setBigDecimal(3, produto.getPreco());

            // 2) Executa o INSERT
            ps.executeUpdate();

            // 3) Recupera a chave gerada (id SERIAL) e seta de volta no objeto
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    produto.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar produto", e);
        }
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        final String sql = "SELECT id, nome, descricao, preco FROM produto WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1) Passa o parâmetro da cláusula WHERE
            ps.setLong(1, id);

            // 2) Executa o SELECT
            try (ResultSet rs = ps.executeQuery()) {

                // 3) Se encontrou, mapeia as colunas para o POJO
                if (rs.next()) {
                    Produto p = new Produto();
                    p.setId(rs.getLong("id"));
                    p.setNome(rs.getString("nome"));
                    p.setDescricao(rs.getString("descricao"));
                    p.setPreco(rs.getBigDecimal("preco"));

                    // 4) Retorna valor presente
                    return Optional.of(p);
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por id", e);
        }
    }

    @Override
    public List<Produto> buscarTodos() {
        final String sql = "SELECT id, nome, descricao, preco FROM produto ORDER BY id";

        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getLong("id"));
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                p.setPreco(rs.getBigDecimal("preco"));
                produtos.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os produtos", e);
        }
        return produtos;
    }

    @Override
    public void atualizar(Produto produto) {
        final String sql = "UPDATE produto SET nome = ?, descricao = ?, preco = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1) Envia os novos valores para o SQL, na ordem exata dos ?
            ps.setString(1, produto.getNome());
            ps.setString(2, produto.getDescricao());
            ps.setBigDecimal(3, produto.getPreco());
            ps.setLong(4, produto.getId());

            // 2) Executa o UPDATE
            int linhasAfetadas = ps.executeUpdate();

            // 3) Caso nennhuma linha seja afetada, o id não existe
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Nenhum produto atualizado: id " + produto.getId() + " não encontrado");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto", e);
        }
    }

    @Override
    public void excluir(Long id) {
        final String sql = "DELETE FROM produto WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 1) Passa o id do registro que queremos excluir
            ps.setLong(1, id);

            // 2) Executa o DELETE e confere quantas linhas foram afetadas
            int linhasAfetadas = ps.executeUpdate();

            // 3) Se não afetou nenhuma linha, o id não existe na tabela
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Nenhum produto excluído: id " + id + " não encontrado");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir produto por id", e);
        }
    }
}
