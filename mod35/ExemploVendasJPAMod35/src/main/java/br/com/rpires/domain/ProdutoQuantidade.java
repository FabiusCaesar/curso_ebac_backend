/**
 * 
 */
package br.com.rpires.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author FabiusCaesar
 * @date 17 de set. de 2025
 */

@Entity
@Table(name = "tb_produto_quantidade")
@SequenceGenerator(name = "sq_prod_qtd_gen", sequenceName = "sq_produto_quantidade", allocationSize = 1)
public class ProdutoQuantidade implements Serializable, br.com.rpires.dao.Persistente {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_prod_qtd_gen")
    @Column(name = "id")
    private Long id;

    // FK: tb_produto_quantidade.id_produto_fk -> tb_produto.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produto_fk", nullable = false)
    private Produto produto;

    // FK: tb_produto_quantidade.id_venda_fk -> tb_venda.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venda_fk", nullable = false)
    private Venda venda;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade = 0;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    public ProdutoQuantidade() { }

    // ===== Regras de negócio (iguais ao original, mantendo o cálculo) =====
    public void adicionar(Integer qtd) {
        this.quantidade += qtd;
        BigDecimal novoValor = this.produto.getValor().multiply(BigDecimal.valueOf(qtd));
        this.valorTotal = this.valorTotal.add(novoValor);
    }

    public void remover(Integer qtd) {
        this.quantidade -= qtd;
        BigDecimal novoValor = this.produto.getValor().multiply(BigDecimal.valueOf(qtd));
        this.valorTotal = this.valorTotal.subtract(novoValor);
    }

    // ===== Utilitário p/ manter o lado dono da relação com Venda =====
    public void setVenda(Venda venda) {
        this.venda = venda;
        // Mantém a coleção sincronizada em memória
        if (venda != null && !venda.getProdutos().contains(this)) {
            venda.getProdutos().add(this);
        }
    }
    
    // ===== Getters/Setters (inclui Persistente) =====
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Venda getVenda() {
		return venda;
	}

}
