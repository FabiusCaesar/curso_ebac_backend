package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */
public class CapitalizeNome {

    // Método utilitário para capitalizar nomes compostos
    public static String capitalizeNome(String nome) {
        String[] partes = nome.toLowerCase().split(" ");
        StringBuilder nomeFinal = new StringBuilder();

        for (String parte : partes) {
            if (!parte.isBlank()) {
                nomeFinal.append(Character.toUpperCase(parte.charAt(0)))
                        .append(parte.substring(1))
                        .append(" ");
            }
        }
        return nomeFinal.toString().trim();
    }
}
