package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */

@Tabela("usuarios")
public class Usuario {
    private String nome;
    private String email;

    // Construtor
    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
