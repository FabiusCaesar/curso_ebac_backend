/**
 * 
 */
package dev.fabiuscaesar.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author FabiusCaesar
 * @date 4 de set. de 2025
 */

@Entity
@Table(name = "tb_acessorio")
@SequenceGenerator(name = "seq_acessorio", sequenceName = "seq_acessorio", allocationSize = 1)
public class Acessorio {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_acessorio")
	private Long id;
		
	@Column(name = "nome", nullable = false, length = 100)
	private String nome;
	
	@Column(name = "descricao", nullable = false, length = 255)
	private String descricao;
	
	
	// Constructor no-arg
	public Acessorio() {}
	
	
	// Getters and Setters
	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public Long getId() {
		return id;
	}
}
