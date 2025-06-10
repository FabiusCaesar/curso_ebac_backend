package dev.fabiuscaesar;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author FabiusCaesar
 */
public class ListaNomesPorSexo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Lê os nomes digitados pelo usuário
        System.out.println("Digite os nomes e sexos no formato nome-sexo separados por vírgula:");
        String entrada = scanner.nextLine();

        // Divide os pares nome-sexo e remove espaços extras
        String[] paresArray = entrada.split(",");

        // Separando por sexo
        List<String> homens = new ArrayList<>();
        List<String> mulheres = new ArrayList<>();

        for (String par : paresArray) {
            // Divide o par em nome e sexo
            String[] partes = par.trim().split("-");

            if (partes.length == 2) {
                String nome = CapitalizeNome.capitalizeNome(partes[0].trim());
                String sexo = partes[1].trim().toUpperCase();

                if (sexo.equals("M")) {
                    homens.add(nome);
                } else if (sexo.equals("F")) {
                    mulheres.add(nome);
                } else {
                    System.out.println("Sexo inválido para: " + nome + ". Ignorado.");
                }
            } else {
                System.out.println("Formato inválido: " + par + ". Ignorado");
            }
        }

        // Ordena as listas
        homens.sort(String.CASE_INSENSITIVE_ORDER);
        mulheres.sort(String.CASE_INSENSITIVE_ORDER);

        // Exibe as listas
        System.out.println();
        System.out.println("##### Homens #####");
        homens.forEach(System.out::println);

        System.out.println();
        System.out.println("##### Mulheres #####");
        mulheres.forEach(System.out::println);

        scanner.close();
    }
}
