package dev.fabiuscaesar.abstractfactory.model;

/**
 * @author FabiusCaesar
 */
public class FiatSUV extends SUV{

    public FiatSUV() {
        this.modelo = "Pulse";
        this.versao = "Drive 1.3";
        this.motor = "1.3 Firefly (aspirado)";
        this.combustivel = "Diesel";
        this.cor = "Vermelho Monte Carlo";
        this.potencia = "107 cv (A) / 98 cv (G) a 6250 rpm";
        this.consumo = "10,4 km/l (A) / 14,7 km/l (G)";
        this.tracaoIntegral = false;
    }
}
