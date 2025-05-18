package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */
public class Aluno {

    // Atributos
    private String nome;
    private double[] notas;

    // Constructor
    public Aluno(String nome, double[] notas) {
        this.nome = nome;
        this.notas = notas;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public double[] getNotas() {
        return notas;
    }
}
