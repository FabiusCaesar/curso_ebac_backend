package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */
public class Main {
    public static void main(String[] args) {
        Class<Usuario> classe = Usuario.class;

        if (classe.isAnnotationPresent(Tabela.class)) {
            Tabela tabela = classe.getAnnotation(Tabela.class);
            System.out.println("Nome da tabela: " + tabela.value());
        } else {
            System.out.println("Annotation @Tabela não encontrada");
        }
    }
}
