package dev.fabiuscaesar;

import java.util.List;

/**
 * @author FabiusCaesar
 */
public class TabelaUtils {

    public static void imprimirTabelaPessoaFisica(List<PessoaFisica> lista) {
        System.out.println("\n======================================================= LISTA DE PESSOAS FISICAS =======================================================");
        System.out.printf("%-10s %-20s %-20s %-15s %-40s\n", "Código", "Nome", "CPF", "Telefone", "Endereço");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");

        for (PessoaFisica pessoa : lista) {
            System.out.printf("%-10s %-20s %-20s %-15s %-40s\n",
                    pessoa.getCodigo(),
                    pessoa.getNome(),
                    pessoa.getCpf(),
                    pessoa.getTelefone(),
                    pessoa.getEndereco());
        }
    }

    public static void imprimirTabelaPessoaJuridica(List<PessoaJuridica> lista) {
        System.out.println("\n====================================================== LISTA DE PESSOAS JURIDICAS ======================================================");
        System.out.printf("%-10s %-25s %-25s %-20s %-15s %-40s\n", "Código", "Razao Social", "Nome Fantasia", "CNPJ", "Telefone", "Endereço");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");

        for (PessoaJuridica empresa : lista) {
            System.out.printf("%-10s %-25s %-25s %-20s %-15s %-40s\n",
                    empresa.getCodigo(),
                    empresa.getRazaoSocial(),
                    empresa.getNomeFantasia(),
                    empresa.getCnpj(),
                    empresa.getTelefone(),
                    empresa.getEndereco());
        }
    }
}
