package dev.fabiuscaesar.abstractfactory;

import dev.fabiuscaesar.abstractfactory.factory.CarFactory;
import dev.fabiuscaesar.abstractfactory.factory.FiatFactory;
import dev.fabiuscaesar.abstractfactory.factory.ToyotaFactory;
import dev.fabiuscaesar.abstractfactory.model.SUV;
import dev.fabiuscaesar.abstractfactory.model.Sedan;

/**
 * @author FabiusCaesar
 */
public class Main {

    public static void main(String[] args) {
        // Criando fábricas
        CarFactory fiatFactory = new FiatFactory();
        CarFactory toyotaFactory = new ToyotaFactory();

        // Criando carros da Fiat
        Sedan fiatSedan = fiatFactory.createSedan();
        SUV fiatSUV = fiatFactory.createSUV();

        // Criando carros da Toyota
        Sedan toyotaSedan = toyotaFactory.createSedan();
        SUV toyotaSUV = toyotaFactory.createSUV();

        // Exibindo informações dos carros
        System.out.println("=== Carros da Fiat ===");
        fiatSedan.exibirInfo();
        System.out.println();
        fiatSUV.exibirInfo();

        System.out.println("\n=== Carros da Toyota ===");
        toyotaSedan.exibirInfo();
        System.out.println();
        toyotaSUV.exibirInfo();
    }
}
