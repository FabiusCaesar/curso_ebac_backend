package dev.fabiuscaesar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FabiusCaesar
 */
public class Main {

    public static void main (String[] args) {

        // Cria listas de Pessoas
        List<PessoaFisica> listaPessoaFisica = new ArrayList<>();
        List<PessoaJuridica> listaPessoaJuridica = new ArrayList<>();

        // Adiciona pessoas físicas com endereços famosos
        listaPessoaFisica.add(new PessoaFisica("001", "Rua Elm, 1428, Springwood, Ohio", "9999-1234", "Nancy Thompson", "999.999.999-11"));
        listaPessoaFisica.add(new PessoaFisica("004", "1640 Riverside Drive, Hill Valley, CA", "66666-1111", "Marty McFly", "111.222.333-44"));
        listaPessoaFisica.add(new PessoaFisica("005", "1646 Riverside Drive, Hill Valley, CA", "66666-2222", "Dr. Emmett Brown", "555.444.333-22"));


        // Adiciona empresas fictícias com endereços famosos
        listaPessoaJuridica.add(new PessoaJuridica("002", "Rua Baker, 221 B, London, UK", "9988-5678", "Holmes & Watson", "Holmes Investigação","89.234.636/0001-88"));
        listaPessoaJuridica.add(new PessoaJuridica("010", "Rua Moore, 14 N, New York, NY", "33333-4444", "Ghostbusters Inc.", "Ghostbusters", "12.345.678/0001-99"));
        listaPessoaJuridica.add(new PessoaJuridica("012", "Lou's Cafe, Courthouse Square, Hill Valley, CA", "55555-6666", "Lou's Cafe", "Lou's Cafe", "11.222.333/0001-44"));
        listaPessoaJuridica.add(new PessoaJuridica("013", "Hill Valley High School, 17259 Mill Road, Hill Valley, CA", "55555-7777", "Hill Valley High School", "Hill Valley High", "22.333.444/0001-55"));
        listaPessoaJuridica.add(new PessoaJuridica("014", "Twin Pines Mall, 18000 Ventura Blvd, Hill Valley, CA", "55555-8888", "Twin Pines Mall", "Twin Pines Mall", "33.444.555/0001-66"));


        // Exibe lista de PessoaFisica
        TabelaUtils.imprimirTabelaPessoaFisica(listaPessoaFisica);

        // Exibe lista de PessoaJuridica
        TabelaUtils.imprimirTabelaPessoaJuridica(listaPessoaJuridica);

    }
}
