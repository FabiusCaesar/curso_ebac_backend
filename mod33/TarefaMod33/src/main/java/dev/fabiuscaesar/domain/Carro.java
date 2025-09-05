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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author FabiusCaesar
 * @date 3 de set. de 2025
 */

@Entity
@Table(name = "tb_carro")
@SequenceGenerator(name = "seq_carro", sequenceName = "seq_carro", allocationSize = 1)
public class Carro {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_carro")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "marca_id", nullable = false)
	private Marca marca;
	
	@Column(name = "modelo", nullable = false, length = 100)
	private String modelo;
	
	@Column(name = "ano")
	private Integer ano;
	
	@ManyToMany
	@JoinTable(
			name = "tb_carro_acessorio",
			joinColumns = @JoinColumn(name = "carro_id"),
			inverseJoinColumns = @JoinColumn(name = "acessorio_id")
			)
	private Set<Acessorio> acessorios = new HashSet<>();
	
	// Constructor no-arg
	public Carro() {}
	

	// Getters and Setters	
	public Marca getMarca() {
		return marca;
	}

	public void setMarca(Marca marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Long getId() {
		return id;
	}


	public Set<Acessorio> getAcessorios() {
		return acessorios;
	}


	public void setAcessorios(Set<Acessorio> acessorios) {
		this.acessorios = acessorios;
	}
}
