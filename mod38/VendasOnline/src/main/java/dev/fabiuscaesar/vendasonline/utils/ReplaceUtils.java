/**
 * 
 */
package dev.fabiuscaesar.vendasonline.utils;

/**
 * Classe utilitária para remoção de caracteres indesejados em strings formatadas.
 * 
 * @author FabiusCaesar
 * @date 14 de out. de 2025
 */

public final class ReplaceUtils {

    // Construtor privado para impedir instanciação
    private ReplaceUtils() {
    }

    /**
     * Remove todos os padrões informados da string de entrada.
     *
     * @param value    String original
     * @param patterns Padrões a serem removidos
     * @return String sem os padrões
     */
    public static String replace(String value, String... patterns) {
        if (value == null) return null;

        String result = value;
        for (String pattern : patterns) {
            result = result.replace(pattern, "");
        }
        return result;
    }
}
