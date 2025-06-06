package dev.fabiuscaesar;

import java.util.ArrayList;
import java.util.List;

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

        // Separando por sexo
        List<Pessoa> homens = new ArrayList<>();
        List<Pessoa> mulheres = new ArrayList<>();

        for (Pessoa p : pessoas) {
            if (p.getSexo().equals("M")) {
                homens.add(p);
            } else if (p.getSexo().equals("F")) {
                mulheres.add(p);
            }
        }

        // Exibindo resultados
        System.out.println("##### Homens #####");
        System.out.println(homens);

        System.out.println(); // linha em branco
        
        System.out.println("##### Mulheres #####");
        System.out.println(mulheres);

    }
}
