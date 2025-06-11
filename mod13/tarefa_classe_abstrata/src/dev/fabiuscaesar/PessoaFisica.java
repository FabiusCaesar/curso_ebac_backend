package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */
public class PessoaFisica extends Pessoa {

    private String nome;

    private String cpf;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public PessoaFisica(String codigo, String endereco, String telefone, String nome, String cpf) {
        super(codigo, endereco, telefone);
        this.nome = nome;
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return "PessoaFisica{" +
                "codigo='" + getCodigo() + '\'' +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", endereco='" + getEndereco() + '\'' +
                ", telefone='" + getTelefone() + '\'' +
                '}';
    }
}
