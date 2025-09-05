/**
 * 
 */
package dev.fabiuscaesar.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author FabiusCaesar
 * @date 2 de set. de 2025
 */

@Entity
@Table(name = "tb_marca")
@SequenceGenerator(name = "seq_marca", sequenceName = "seq_marca", allocationSize = 1)
public class Marca {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_marca")
	private Long id;
	
	@Column(name = "nome", nullable = false, length = 100)
	private String nome;
	
	@OneToMany(mappedBy = "marca")
	private Set<Carro> carros = new HashSet<>();
	
	
	// Constructor no-arg
	public Marca() {}

	
	// Getters and Setters
	public Long getId() {
		return id;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


	public Set<Carro> getCarros() {
		return carros;
	}	
}
