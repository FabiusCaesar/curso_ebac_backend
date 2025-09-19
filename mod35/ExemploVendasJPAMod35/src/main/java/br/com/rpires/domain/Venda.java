/**
 * 
 */
package br.com.rpires.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author FabiusCaesar
 * @date 16 de set. de 2025
 */

@Entity
@Table(name = "tb_venda")
@SequenceGenerator(name = "sq_venda_gen", sequenceName = "sq_venda", allocationSize = 1)
public class Venda implements Serializable, br.com.rpires.dao.Persistente {
    private static final long serialVersionUID = 1L;

    public enum Status {
        INICIADA, CONCLUIDA, CANCELADA;

        public static Status getByName(String value) {
            for (Status status : Status.values()) {
                if (status.name().equals(value)) {
                    return status;
                }
            }
            return null;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_venda_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo", nullable = false, length = 10, unique = true)
    private String codigo;

    // FK: tb_venda.id_cliente_fk -> tb_cliente.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente_fk",nullable = false)
    private Cliente cliente;

    // Relacionamento 1:N com os itens da venda;
    // orphanRemoval = true para refletir a remoção da coleção no banco.
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProdutoQuantidade> produtos = new HashSet<>();

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    // TIMESTAMPTZ -> java.time.Instant (Hibernate 5.6 suporta)
    @Column(name = "data_venda", nullable = false)
    private Instant dataVenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_venda", nullable = false, length = 50)
    private Status status;

    public Venda() { }

    // ======= Regras de negócio (mantidas do seu original) =======
    public void adicionarProduto(Produto produto, Integer quantidade) {
        validarStatus();
        Optional<ProdutoQuantidade> op =
                produtos.stream().filter(pq -> pq.getProduto().getCodigo().equals(produto.getCodigo())).findAny();
        if (op.isPresent()) {
            ProdutoQuantidade prodQtd = op.get();
            prodQtd.adicionar(quantidade);
        } else {
            ProdutoQuantidade prod = new ProdutoQuantidade();
            prod.setProduto(produto);
            prod.adicionar(quantidade);
            
            prod.setVenda(this);
            produtos.add(prod);
        }
        recalcularValorTotalVenda();
    }

    public void removerProduto(Produto produto, Integer quantidade) {
        validarStatus();
        Optional<ProdutoQuantidade> op =
                produtos.stream().filter(pq -> pq.getProduto().getCodigo().equals(produto.getCodigo())).findAny();
        if (op.isPresent()) {
            ProdutoQuantidade prodQtd = op.get();
            if (prodQtd.getQuantidade() > quantidade) {
                prodQtd.remover(quantidade);
                recalcularValorTotalVenda();
            } else {
                produtos.remove(op.get());
                recalcularValorTotalVenda();
            }
        }
    }

    public void removerTodosProdutos() {
        validarStatus();
        produtos.clear();
        valorTotal = BigDecimal.ZERO;
    }

    public Integer getQuantidadeTotalProdutos() {
        return produtos.stream().reduce(0,
                (acc, pq) -> acc + pq.getQuantidade(),
                Integer::sum);
    }

    public void recalcularValorTotalVenda() {
        BigDecimal total = BigDecimal.ZERO;
        for (ProdutoQuantidade pq : this.produtos) {
            total = total.add(pq.getValorTotal());
        }
        this.valorTotal = total;
    }

    private void validarStatus() {
        if (this.status == Status.CONCLUIDA) {
            throw new UnsupportedOperationException("IMPOSSÍVEL ALTERAR VENDA FINALIZADA");
        }
    }

    // ======= Defaults automáticos ao persistir (opcional, ajuda nos testes) =======
    @PrePersist
    protected void onCreate() {
        if (dataVenda == null) dataVenda = Instant.now();
        if (status == null) status = Status.INICIADA;
        if (valorTotal == null) valorTotal = BigDecimal.ZERO;
    }

    // ======= equals/hashCode por 'codigo' (único de negócio) =======
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venda)) return false;
        Venda other = (Venda) o;
        return this.codigo != null && this.codigo.equals(other.codigo);
    }

    @Override
    public int hashCode() {
        return (codigo == null ? 0 : codigo.hashCode());
    }

    // ======= Getters/Setters (inclui contrato Persistente) =======
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Set<ProdutoQuantidade> getProdutos() {
		return produtos;
	}

	public void setProdutos(Set<ProdutoQuantidade> produtos) {
		this.produtos = produtos;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Instant getDataVenda() {
		return dataVenda;
	}

	public void setDataVenda(Instant dataVenda) {
		this.dataVenda = dataVenda;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
