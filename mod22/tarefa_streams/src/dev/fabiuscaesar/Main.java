package dev.fabiuscaesar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author FabiusCaesar
 */
public class Main {
    public static void main(String[] args) {
        List<Pessoa> pessoas = new ArrayList<>();

        // Adicionando pessoas
        pessoas.add(new Pessoa("Júlio César", "M"));
        pessoas.add(new Pessoa("Cleópatra", "F"));
        pessoas.add(new Pessoa("Marco Antônio", "M"));
        pessoas.add(new Pessoa("Lívia Drusa", "F"));
        pessoas.add(new Pessoa("Augusto", "M"));

        // Filtrar apenas mulheres e extrair os nomes
        List<String> listaDeMulheres = pessoas.stream()
                .filter(p -> p.getSexo().equals("F"))   // filtra só mulheres
                .map(Pessoa::getNome)                          // pega apenas o nome
                .collect(Collectors.toList());                 // transforma em lista de Strings

        // Exibe a lista
        System.out.println("Nomes das mulheres:");
        System.out.println(listaDeMulheres);

    }
}
