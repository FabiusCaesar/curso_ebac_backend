/**
 * 
 */
package dev.fabiuscaesar.vendasonline.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Column;


/**
 * @author FabiusCaesar
 * @date 15 de set. de 2025
 */


@Entity
@Table(
	name = "tb_produto",
	uniqueConstraints = @UniqueConstraint(name = "uk_produto_codigo", columnNames = "codigo")
)
@SequenceGenerator(name = "sq_produto_gen", sequenceName = "sq_produto", allocationSize = 1)
// >>> adiciona os named queries como no padrão do professor <<<
@NamedQueries({
    @NamedQuery(name = "Produto.findByNome",
                query = "SELECT p FROM Produto p WHERE p.nome LIKE :nome"),
    @NamedQuery(name = "Produto.findByCodigo",
                query = "SELECT p FROM Produto p WHERE p.codigo = :codigo")
})
public class Produto implements Serializable, Persistente {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_produto_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo", nullable = false, length = 10)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "modelo", nullable = false, length = 50)
    private String modelo;

    @Column(name = "descricao", nullable = false, length = 100)
    private String descricao;

    // numeric(10,2) -> BigDecimal com precision/scale
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    // === Construtores ===
    public Produto() { } // exigido pela JPA e usado em testes/reflexão

    public Produto(String codigo, String nome, String modelo, String descricao, BigDecimal valor) {
        this.codigo = codigo;
        this.nome = nome;
        this.modelo = modelo;
        this.descricao = descricao;
        this.valor = valor;
    }


    // === Getters/Setters (inclui contrato Persistente) ===
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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    // === equals/hashCode por "codigo" (chave natural única) ===
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produto)) return false;
        Produto other = (Produto) o;
        return this.codigo != null && this.codigo.equals(other.codigo);
    }
    
	@Override
    public int hashCode() {
        return (codigo == null ? 0 : codigo.hashCode());
    }
}