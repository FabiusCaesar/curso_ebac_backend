package dev.fabiuscaesar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author FabiusCaesar
 */
public class Main {
    public static void main(String[] args) {
        List<Pessoa> pessoas = criarListaDePessoas();
        List<String> listaDeMulheres = filtrarMulheres(pessoas);

        // Exibe a lista
        System.out.println("Nomes das mulheres:");
        System.out.println(listaDeMulheres);

    }

    /**
     * Cria uma lista fixa de pessoas (dados simulados).
     */
    public static List<Pessoa> criarListaDePessoas() {
        List<Pessoa> pessoas = new ArrayList<>();
        pessoas.add(new Pessoa("Júlio César", "M"));
        pessoas.add(new Pessoa("Cleópatra", "F"));
        pessoas.add(new Pessoa("Marco Antônio", "M"));
        pessoas.add(new Pessoa("Lívia Drusa", "F"));
        pessoas.add(new Pessoa("Augusto", "M"));
        return pessoas;
    }

    /**
     * Recebe uma lista de pessoas e retorna uma lista com os nomes das mulheres.
     */
    public static List<String> filtrarMulheres(List<Pessoa> pessoas) {
        return pessoas.stream()
            .filter(p -> p.getSexo().equals("F"))   // filtra só mulheres
            .map(Pessoa::getNome)                          // pega apenas o nome
            .collect(Collectors.toList());                 // transforma em lista de Strings
    }
}
