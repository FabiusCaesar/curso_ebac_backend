package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */
public class Pessoa {
    private String nome;
    private String sexo; // "M" para masculino, "F" para feminino

    public Pessoa(String nome, String sexo) {
        this.nome = nome;
        this.sexo = sexo.toUpperCase();
    }

    public String getNome() {
        return nome;
    }

    public String getSexo() {
        return sexo;
    }

    @Override
    public String toString() {
        return nome;
    }
}
