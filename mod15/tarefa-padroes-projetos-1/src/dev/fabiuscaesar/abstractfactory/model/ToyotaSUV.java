package dev.fabiuscaesar.abstractfactory.model;

/**
 * @author FabiusCaesar
 */
public class ToyotaSUV extends SUV{

    public ToyotaSUV() {
        this.modelo = "SW4";
        this.versao = "SRX Platinum 2.8";
        this.motor = "2.8L 16V Turbo diesel";
        this.combustivel = "Diesel";
        this.cor = "Preta";
        this.potencia = "204 cv a 3400 rpm";
        this.consumo = "10,5 km/l";
        this.tracaoIntegral = true;
    }
}
