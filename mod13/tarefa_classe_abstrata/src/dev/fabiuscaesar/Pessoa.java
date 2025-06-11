package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */
public abstract class Pessoa {

    private String codigo;

    private String endereco;

    private String telefone;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Pessoa(String codigo, String endereco, String telefone) {
        this.codigo = codigo;
        this.endereco = endereco;
        this.telefone = telefone;
    }
}
