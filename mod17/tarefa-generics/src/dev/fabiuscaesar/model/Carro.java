package dev.fabiuscaesar.model;

/**
 * @author FabiusCaesar
 */
public class Carro extends Veiculo{
    private int portas;
    private String tipocombustivel;

    public Carro(String modelo, String fabricante, String cor, int ano, int portas, String tipocombustivel) {
        super(modelo, fabricante, cor, ano);
        this.portas = portas;
        this.tipocombustivel = tipocombustivel;
    }

    public int getPortas() {
        return portas;
    }

    public String getTipocombustivel() {
        return tipocombustivel;
    }

    @Override
    protected void exibirEspecificos() {
        System.out.println("Portas: " + portas);
        System.out.println("Combustível: " + tipocombustivel);
    }
}
