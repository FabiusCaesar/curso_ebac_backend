package br.com.rpires.dao;

import br.com.rpires.dao.generic.GenericDAO;
import br.com.rpires.domain.Estoque;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.EstoqueInsuficienteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author FabiusCaesar
 */
public class EstoqueDAO extends GenericDAO<Estoque, Long> implements IEstoqueDAO {

    @Override
    public void incrementar(Long produtoId, int quantidade) throws DAOException {
        // 1) guarda de entrada
        if (quantidade <= 0) return;

        // 2) declare Connection e PreparedStatement fora do try/catch (para fechar no finally)
        Connection connection = null;
        PreparedStatement stm = null;

        try {
            // 3) abrir conexão (getConnection())
            connection = getConnection();

            // 4) preparar o SQL de UPDATE (com dois ?)
            String sql = "UPDATE tb_estoque SET quantidade = quantidade + ?, updated_at = NOW() WHERE produto_id = ?";
            stm = connection.prepareStatement(sql);

            // 5) bind dos parâmetros: (1) quantidade, (2) produtoId
            stm.setInt(1, quantidade);
            stm.setLong(2, produtoId);

            // 6) executeUpdate e capture rowsAffected
            int rowsAffected = stm.executeUpdate();

            // 7) se rowsAffected == 0 -> lançar DAOException("Estoque não encontrado para produto " + produtoId)
            if (rowsAffected == 0) {
                throw new DAOException("Estoque não encontrado para produto " + produtoId,
                        new Exception("Nenhuma linha foi atualizada no UPDATE de incremento")
                );
            }

        } catch (SQLException e) {
            // 8) encapsular em DAOException com mensagem clara
            throw new DAOException("Erro incrementando estoque do produto " + produtoId, e);
        } finally {
            // 9) fechar recursos com closeConnection(connection, stm, null)
            closeConnection(connection, stm, null);
        }

    }

    @Override
    public void decrementarOuFalhar(Long produtoId, int quantidade)
            throws DAOException, EstoqueInsuficienteException {

        // 1) guarda de entrada
        if (quantidade <= 0) return;

        // 2) Connection e PreparedStatement fora do try/catch (para fechar no finally)
        Connection connection = null;
        PreparedStatement stm = null;
        PreparedStatement checkStm = null;
        ResultSet rs = null;

        try {
            // 3) abrir conexão (getConnection())
            connection = getConnection();

            // 4) UPDATE com guarda de saldo
            String sql = "UPDATE tb_estoque SET quantidade = quantidade - ?, updated_at = NOW() WHERE produto_id = ? AND quantidade >= ?";

            stm = connection.prepareStatement(sql);

            // 5) bind dos parâmetros: (1) quantidade, (2) produtoId
            stm.setInt(1, quantidade);
            stm.setLong(2, produtoId);
            stm.setInt(3, quantidade);

            // 6) executeUpdate e capture rowsAffected
            int rowsAffected = stm.executeUpdate();

            // 7) se rowsAffected == 0
            if (rowsAffected == 0) {
                // Descobrir o motivo: não existe registro ou saldo insuficiente?
                String check = "SELECT 1 FROM tb_estoque WHERE produto_id = ?";
                checkStm = connection.prepareStatement(check);
                checkStm.setLong(1, produtoId);
                rs = checkStm.executeQuery();

                if (!rs.next()) {
                    // não existe linha de estoque para esse produto
                    throw new DAOException("Estoque não encontrado para produto " + produtoId,
                            new Exception("Nenhuma linha foi atualizada no UPDATE de decremento"));
                } else {
                    // existe registro, então o motivo foi saldo insuficiente
                    throw new EstoqueInsuficienteException(
                            "Estoque insuficiente para produto " + produtoId +
                                    " (solicitado=" + quantidade + ")"
                    );
                }
            }

        } catch (SQLException e) {
            // 8) encapsular em DAOException com mensagem clara
            throw new DAOException("Erro decrementando estoque do produto " + produtoId, e);
        } finally {
            // 9) fechar recursos com closeConnection(connection, stm, null)
            closeConnection(null, checkStm, rs);
            closeConnection(connection, stm, null);
        }
    }

    @Override
    public Class<Estoque> getTipoClasse() {
        return Estoque.class;
    }

    @Override
    public void atualiarDados(Estoque entity, Estoque entityCadastrado) {
        entityCadastrado.setQuantidade(entity.getQuantidade());
        entityCadastrado.setUpdatedAt(entity.getUpdatedAt());
    }

    @Override
    protected String getQueryInsercao() {
        return "INSERT INTO tb_estoque (produto_id, quantidade, updated_at) VALUES (?, ?, NOW())";
    }

    @Override
    protected String getQueryExclusao() {
        return "DELETE FROM tb_estoque WHERE produto_id = ?";
    }

    @Override
    protected String getQueryAtualizacao() {
        return "UPDATE tb_estoque SET quantidade = ?, updated_at = NOW() WHERE produto_id = ?";
    }

    @Override
    protected void setParametrosQueryInsercao(PreparedStatement stmInsert, Estoque entity) throws SQLException {
        stmInsert.setLong(1, entity.getId());          // produto_id (já deve vir preenchido)
        stmInsert.setInt(2, entity.getQuantidade());   // quantidade
        // updated_at = NOW()
    }

    @Override
    protected void setParametrosQueryExclusao(PreparedStatement stmDelete, Long valor) throws SQLException {
        // valor = produto_id
        stmDelete.setLong(1, valor);
    }

    @Override
    protected void setParametrosQueryAtualizacao(PreparedStatement stmUpdate, Estoque entity) throws SQLException {
        stmUpdate.setInt(1, entity.getQuantidade());   // quantidade
        stmUpdate.setLong(2, entity.getId());          // WHERE produto_id = ?
    }

    @Override
    protected void setParametrosQuerySelect(PreparedStatement stmSelect, Long valor) throws SQLException {
        // usado pelo GenericDAO.consultar()
        stmSelect.setLong(1, valor); // WHERE <pk> = ?
    }
}
