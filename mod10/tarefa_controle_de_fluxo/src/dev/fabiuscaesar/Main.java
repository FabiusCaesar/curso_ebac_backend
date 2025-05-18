package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */
public class Main {
    public static void main(String[] args) {
        double mediaDeAprovacao = 7.0;
        double mediaDeRecuperacao = 5.0;

        Aluno lucius   = new Aluno("Lucius", new double[]{7, 8, 9, 10});
        Aluno cassius  = new Aluno("Cassius", new double[]{3.5, 4, 2.5});
        Aluno octavia  = new Aluno("Octavia", new double[]{5.5, 6.5, 6, 5});
        Aluno marcus   = new Aluno("Marcus", new double[]{7, 7, 7, 7});
        Aluno livia    = new Aluno("Livia", new double[]{5.5, 7.5, 9, 5});
        Aluno flavius  = new Aluno("Flavius", new double[]{4.9, 3, 4});
        Aluno aemilia  = new Aluno("Aemilia", new double[]{6, 10, 7.75, 5.0});
        Aluno agrippa  = new Aluno("Agrippa", new double[]{5, 5, 5, 5});
        Aluno julius   = new Aluno("Julius", new double[]{9, 8, 9.5, 10});
        Aluno titus    = new Aluno("Titus", new double[]{7.5, 4, 10, 2});


        Aluno[] alunos = {
                lucius, cassius, octavia, marcus, livia,
                flavius, aemilia, agrippa, julius, titus
        };

        // Cabeçalho da "tabela"
        System.out.printf("%-10s | %-12s | %-10s%n", "Aluno", "Média Final", "Situação");
        System.out.println("===========|==============|===========");

        for (Aluno aluno : alunos) {
            NotasEMedia avaliador = new NotasEMedia();
            avaliador.setNotas(aluno.getNotas());
            avaliador.setMediaDeAprovacao(mediaDeAprovacao);
            avaliador.setMediaDeRecuperação(mediaDeRecuperacao);

            double media = avaliador.calculoDaMedia();
            String situacao = avaliador.verificarAprovacao(media);

            System.out.printf("%-10s | %-12.1f | %-10s%n", aluno.getNome(), media, situacao);
            System.out.println("-----------|--------------|-----------");
        }
    }
}
