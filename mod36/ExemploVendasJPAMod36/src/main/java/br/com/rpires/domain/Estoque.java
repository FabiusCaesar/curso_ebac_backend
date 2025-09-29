/**
 * 
 */
package br.com.rpires.domain;

import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import br.com.rpires.dao.Persistente;

/**
 * @author FabiusCaesar
 * @date 17 de set. de 2025
 */

@Entity
@Table(name = "tb_estoque")
public class Estoque implements Persistente {

    @Id
    @Column(name = "produto_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Estoque() {}

    public Estoque(Produto produto, Integer quantidade) {
        this.produto = produto;
        this.id = (produto != null ? produto.getId() : null); // PK compartilhada
        this.quantidade = quantidade;
    }
    
    public Estoque(Long produtoId, Integer quantidade) {
        this.id = produtoId;
        this.quantidade = quantidade;
    }

    @PrePersist
    @PreUpdate
    private void touch() {
        this.updatedAt = Instant.now();
    }

    // Getters/Setters
    public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Estoque)) return false;
        Estoque estoque = (Estoque) o;
        return Objects.equals(id, estoque.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Estoque{" +
                "produtoId=" + id +
                ", quantidade=" + quantidade +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
