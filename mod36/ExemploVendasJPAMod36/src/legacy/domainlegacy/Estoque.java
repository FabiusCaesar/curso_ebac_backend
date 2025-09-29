package br.com.rpires.domain.legacy;

import anotacao.ColunaTabela;
import anotacao.Tabela;
import anotacao.TipoChave;
import br.com.rpires.dao.Persistente;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author FabiusCaesar
 */
@Tabela("TB_ESTOQUE")
public class Estoque  implements Persistente {

    @TipoChave("getId")
    @ColunaTabela(dbName = "produto_id", setJavaName = "setId")
    private Long id;

    @ColunaTabela(dbName = "quantidade", setJavaName = "setQuantidade")
    private Integer quantidade;

    private Timestamp updatedAt;

    public Estoque() {}

    public Estoque(Long produtoId, Integer quantidade) {
        this.id = produtoId;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estoque estoque = (Estoque) o;
        return Objects.equals(id, estoque.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Estoque{" +
                "produtoId=" + id +
                ", quantidade=" + quantidade +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
