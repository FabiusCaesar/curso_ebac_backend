package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */
public class Main {
    public static void main(String[] args) {
        WrapperConverter converter = new WrapperConverter();

        // Título do programa
        System.out.println();
        System.out.println("=== dev.fabiuscaesar | Projeto Wrapper ===");
        System.out.println("=== Conversão de tipo primitivo para Wrapper ===");
        System.out.println();

        System.out.println("Valor primitivo:");
        System.out.println("-> " + converter.getPrimitiveValue());

        System.out.println("----------------------------------------");

        System.out.println("Valor wrapper:");
        System.out.println("-> " + converter.getWrapperValue());

        System.out.println();
        System.out.println("========================================");

    }
}
