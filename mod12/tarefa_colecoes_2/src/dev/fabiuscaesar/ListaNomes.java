package dev.fabiuscaesar;

import java.util.*;

/**
 * @author FabiusCaesar
 */
public class ListaNomes {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Lê os nomes digitados pelo usuário
        System.out.println("Digite os nomes separados por vírgula:");
        String entrada = scanner.nextLine();

        // Divide a string em partes (nomes), remove espaços extras
        String[] nomesArray = entrada.split(",");
        List<String> nomes = new ArrayList<>();

        for (String nome : nomesArray) {
            nomes.add(CapitalizeNome.capitalizeNome(nome.trim()));
        }

        // Ordena a lista ignorando maiúsculas/minúsculas
        nomes.sort(String.CASE_INSENSITIVE_ORDER);

        // Exibe a lista ordenada
        System.out.println();
        System.out.println("##### Lista de Nomes Ordenada #####");
        nomes.forEach(System.out::println);

        scanner.close();
    }

}
