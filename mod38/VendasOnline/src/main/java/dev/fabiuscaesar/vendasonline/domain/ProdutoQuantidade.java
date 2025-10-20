/**
 * 
 */
package dev.fabiuscaesar.vendasonline.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;

/**
 * @author FabiusCaesar
 * @date 11 de out. de 2025
 */

@Entity
@Table(
    name = "tb_produto_quantidade",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_prod_qtd_venda_prod",
        columnNames = { "id_venda", "id_produto" }
    )
)
public class ProdutoQuantidade implements Serializable, Persistente {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_prod_qtd")
    @SequenceGenerator(name = "sq_prod_qtd", sequenceName = "sq_prod_qtd", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produto", foreignKey = @ForeignKey(name = "fk_prod_qtd_prod"))
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venda", foreignKey = @ForeignKey(name = "fk_prod_qtd_venda"))
    private Venda venda;

    @Column(nullable = false)
    private Integer quantidade = 0;

    @Column(name = "valor_total", precision = 19, scale = 2, nullable = false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    public ProdutoQuantidade() {}

    public ProdutoQuantidade(Venda venda, Produto produto, Integer quantidade) {
        this.venda = venda;
        this.produto = produto;
        this.quantidade = 0;
        this.valorTotal = BigDecimal.ZERO;
        adicionar(quantidade);
    }

    public void adicionar(Integer qtd) {
        this.quantidade += qtd;
        BigDecimal inc = produto.getValor().multiply(BigDecimal.valueOf(qtd));
        this.valorTotal = this.valorTotal.add(inc);
    }

    public void remover(Integer qtd) {
        this.quantidade -= qtd;
        BigDecimal dec = produto.getValor().multiply(BigDecimal.valueOf(qtd));
        this.valorTotal = this.valorTotal.subtract(dec);
    }

    // Getters/Setters
    public Long getId() { return id; }
    @Override public void setId(Long id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Venda getVenda() { return venda; }
    public void setVenda(Venda venda) { this.venda = venda; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}
