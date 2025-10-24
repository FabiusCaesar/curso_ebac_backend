/**
 * 
 */
package dev.fabiuscaesar.vendasonline.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.ForeignKey;
import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;

import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * @author FabiusCaesar
 * @date 12 de out. de 2025
 */

@Entity
@Table(
  name = "tb_estoque",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_estoque_produto", columnNames = {"id_produto"})
  }
)
@SequenceGenerator(name="sq_estoque_gen", sequenceName="sq_estoque", allocationSize=1)
public class Estoque implements Persistente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_estoque_gen")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produto", nullable = false,
                foreignKey = @ForeignKey(name = "fk_estoque_produto"))
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Estoque() {}

    public Estoque(Produto produto, Integer quantidade) {
        this.produto = produto;
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
