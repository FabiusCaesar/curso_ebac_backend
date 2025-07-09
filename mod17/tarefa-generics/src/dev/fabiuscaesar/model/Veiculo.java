package dev.fabiuscaesar.model;

/**
 * @author FabiusCaesar
 */
public abstract class Veiculo implements Exibivel{
    private String modelo;
    private String fabricante;
    private String cor;
    private int ano;

    public Veiculo(String modelo, String fabricante, String cor, int ano) {
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.cor = cor;
        this.ano = ano;
    }

    public String getModelo() {
        return modelo;
    }

    public String getFabricante() {
        return fabricante;
    }

    public String getCor() {
        return cor;
    }

    public int getAno() {
        return ano;
    }

    // Template Method - não pode ser sobrescrito
    @Override
    public final void exibirFichaTecnica() {
        System.out.println("Modelo: " + modelo);
        System.out.println("Fabricante: " + fabricante);
        System.out.println("Cor: " + cor);
        System.out.println("Ano: " + ano);
        exibirEspecificos(); // cada subclasse define sua parte
    }

    // Parte específica deixada para a subclasse
    protected abstract void exibirEspecificos();
}
