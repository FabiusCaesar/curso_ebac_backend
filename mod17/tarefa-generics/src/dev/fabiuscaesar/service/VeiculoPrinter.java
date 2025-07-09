package dev.fabiuscaesar.service;

import dev.fabiuscaesar.model.Veiculo;

import java.util.List;

/**
 * @author FabiusCaesar
 */
public class VeiculoPrinter {

    public static void imprimirFichas(List<? extends Veiculo> listaDeVeiculos) {
        for (Veiculo veiculo : listaDeVeiculos){
            veiculo.exibirFichaTecnica();
            System.out.println("---------------------------");
        }
    }
}
