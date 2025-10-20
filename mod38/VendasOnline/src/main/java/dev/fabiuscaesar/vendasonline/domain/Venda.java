/**
 * 
 */
package dev.fabiuscaesar.vendasonline.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.persistence.*;

/**
 * @author FabiusCaesar
 * @date 11 de out. de 2025
 */

@Entity
@Table(name = "tb_venda",
       uniqueConstraints = @UniqueConstraint(name = "uk_venda_codigo", columnNames = "codigo"))
// JPQL simples; o gráfico cuida dos FETCHes
@NamedQuery(name = "Venda.findByCodigo",
            query = "SELECT v FROM Venda v WHERE v.codigo = :codigo")
// >>> EntityGraph: carrega produtos e, dentro deles, o produto
@NamedEntityGraph(
    name = "Venda.comProdutosEProduto",
    attributeNodes = @NamedAttributeNode(value = "produtos", subgraph = "sgProdutos"),
    subgraphs = {
        @NamedSubgraph(name = "sgProdutos",
            attributeNodes = @NamedAttributeNode("produto"))
    }
)
public class Venda implements Serializable, Persistente {
	
	private static final long serialVersionUID = 1L;

    public enum Status { INICIADA, CONCLUIDA, CANCELADA }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_venda")
    @SequenceGenerator(name = "sq_venda", sequenceName = "sq_venda", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 20)
    private String codigo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_cliente", foreignKey = @ForeignKey(name = "fk_venda_cliente"))
    private Cliente cliente;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProdutoQuantidade> produtos = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.INICIADA;

    @Column(name = "valor_total", precision = 19, scale = 2, nullable = false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "data_venda", nullable = false)
    private Instant dataVenda = Instant.now();

    public Venda() {}
    public Venda(String codigo, Cliente cliente) {
        this.codigo = codigo;
        this.cliente = cliente;
    }
    
    @Transient
    public Date getDataVendaAsDate() {
        return dataVenda == null ? null : Date.from(dataVenda);
    }

    /* Regras ~ idênticas ao legado */
    public void adicionarProduto(Produto p, int quantidade) {
        validarStatus();
        Optional<ProdutoQuantidade> op = produtos.stream()
            .filter(pq -> pq.getProduto().getId().equals(p.getId()))
            .findFirst();
        if (op.isPresent()) {
            op.get().adicionar(quantidade);
        } else {
            ProdutoQuantidade novo = new ProdutoQuantidade(this, p, quantidade);
            produtos.add(novo);
        }
        recalcularValorTotalVenda();
    }

    public void removerProduto(Produto p, int quantidade) {
        validarStatus();
        Optional<ProdutoQuantidade> op = produtos.stream()
            .filter(pq -> pq.getProduto().getId().equals(p.getId()))
            .findFirst();
        if (op.isPresent()) {
            ProdutoQuantidade pq = op.get();
            if (pq.getQuantidade() > quantidade) {
                pq.remover(quantidade);
            } else {
                produtos.remove(pq);
            }
            recalcularValorTotalVenda();
        }
    }

    public void removerTodosProdutos() {
        validarStatus();
        produtos.clear();
        valorTotal = BigDecimal.ZERO;
    }

    public int getQuantidadeTotalProdutos() {
        return produtos.stream().mapToInt(ProdutoQuantidade::getQuantidade).sum();
    }

    public void finalizar() {
        validarStatus();
        this.status = Status.CONCLUIDA;
    }

    public void cancelar() {
        validarStatus();
        removerTodosProdutos();
        this.status = Status.CANCELADA;
    }

    private void validarStatus() {
        if (status == Status.CONCLUIDA || status == Status.CANCELADA) {
            throw new UnsupportedOperationException("Venda já finalizada/cancelada");
        }
    }

    private void recalcularValorTotalVenda() {
        this.valorTotal = produtos.stream()
            .map(ProdutoQuantidade::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters/Setters
    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Set<ProdutoQuantidade> getProdutos() { return produtos; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public Instant getDataVenda() { return dataVenda; }
    public void setDataVenda(Instant dataVenda) { this.dataVenda = dataVenda; }

    // Igual ao legado: igualdade por codigo (chave de negócio)
    @Override public int hashCode() { return (codigo == null ? 0 : codigo.hashCode()); }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venda)) return false;
        Venda v = (Venda) o;
        return codigo != null && codigo.equals(v.codigo);
    }
	@Override
	public void setId(Long id) { this.id = id; }
}

