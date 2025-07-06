package dev.fabiuscaesar.abstractfactory.model;

/**
 * @author FabiusCaesar
 */
public abstract class Car {
    protected String modelo;
    protected String versao;
    protected String categoria;
    protected String motor;
    protected String combustivel;
    protected String cor;
    protected String potencia;
    protected String consumo;

    public void exibirInfo() {
        System.out.println("- " + modelo + " " + versao + " -");
        System.out.println("Categoria: " + categoria);
        System.out.println("Motor: " + motor);
        System.out.println("Combustível: " + combustivel);
        System.out.println("Cor: " + cor);
        System.out.println("Potência: " + potencia);
        System.out.println("Consumo: " + consumo);
    }
}
