package dev.fabiuscaesar;

import dev.fabiuscaesar.model.Carro;
import dev.fabiuscaesar.model.Moto;
import dev.fabiuscaesar.service.VeiculoPrinter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FabiusCaesar
 */
public class Main {
    public static void main(String[] args) {
        List<Carro> carros = new ArrayList<>();
        carros.add(new Carro("Mustang Fastback", "Ford", "Verde Escuro", 1968, 2, "Gasolina"));
        carros.add(new Carro("Fusca 1300", "Volkswagen", "Azul Pastel", 1974, 2, "Gasolina"));
        carros.add(new Carro("Civic EX", "Honda", "Cinza", 2020, 4, "Flex"));

        List<Moto> motos = new ArrayList<>();
        motos.add(new Moto("CG 125", "Honda", "Prata", 1983, false, 125));
        motos.add(new Moto("RD 350", "Yamaha", "Roxa", 1987, true, 350));
        motos.add(new Moto("Ninja 400", "Kawasaki", "Verde", 2022, true, 399));

        System.out.println("=== Fichas Técnicas de Carros ===");
        VeiculoPrinter.imprimirFichas(carros);

        System.out.println("=== Fichas Técnicas de Motos ===");
        VeiculoPrinter.imprimirFichas(motos);
    }
}
