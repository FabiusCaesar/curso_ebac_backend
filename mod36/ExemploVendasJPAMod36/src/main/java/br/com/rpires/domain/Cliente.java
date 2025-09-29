/**
 * 
 */
package br.com.rpires.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.rpires.dao.Persistente;

/**
 * @author FabiusCaesar
 * @date 13 de set. de 2025
 */

@Entity
@Table(name = "tb_cliente")
@SequenceGenerator(name = "sq_cliente_gen", sequenceName = "sq_cliente", allocationSize = 1)
public class Cliente implements Serializable, Persistente {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_cliente_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "cpf", nullable = false)
    private Long cpf;

    @Column(name = "tel", nullable = false)
    private Long tel;

    @Column(name = "cep", nullable = false, length = 9)
    private String cep;

    @Column(name = "endereco", nullable = false, length = 50)
    private String end;

    @Column(name = "numero", nullable = false)
    private Long numero;

    @Column(name = "cidade", nullable = false, length = 50)
    private String cidade;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    public Cliente() {}

    public Cliente(String nome, Long cpf, Long tel, String cep, String end,
                   Long numero, String cidade, String estado) {
        this.nome = nome;
        this.cpf = cpf;
        this.tel = tel;
        this.cep = cep;
        this.end = end;
        this.numero = numero;
        this.cidade = cidade;
        this.estado = estado;
    }
    

    // Getters/Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Long getTel() {
		return tel;
	}

	public void setTel(Long tel) {
		this.tel = tel;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	// OVERLOAD para compatibilidade com testes que passam int/Integer
    public void setNumero(int numero) { 
        this.numero = Long.valueOf(numero); 
    }

    public void setNumero(Integer numero) { 
        this.numero = (numero == null ? null : numero.longValue()); 
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente other = (Cliente) o;
        // Igualdade por chave natural de negócio (CPF é único na tabela)
        return this.cpf != null && this.cpf.equals(other.cpf);
    }

    @Override
    public int hashCode() {
        // Consistente com equals baseado em CPF
        return (cpf == null ? 0 : cpf.hashCode());
    }

}
