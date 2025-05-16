package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */
public class Main {
    public static void main(String[] args) {
        double mediaDeAprovacao = 6.0;

        Aluno lucius = new Aluno("Lucius", new double[]{7, 8, 9});
        Aluno aemilia = new Aluno("Aemilia", new double[]{6, 10});
        Aluno livia = new Aluno("Livia", new double[]{5.5, 7.5, 9, 5});
        Aluno titus = new Aluno("Titus", new double[]{7.5, 4, 10});

        Aluno[] alunos = {lucius, aemilia, livia, titus};

        // Cabeçalho da "tabela"
        System.out.printf("%-10s | %-12s | %-10s%n", "Aluno", "Média Final", "Situação");
        System.out.println("===========|==============|===========");

        for (Aluno aluno : alunos) {
            NotasEMedia avaliador = new NotasEMedia();
            avaliador.setNotas(aluno.getNotas());
            avaliador.setMediaDeAprovacao(mediaDeAprovacao);

            double media = avaliador.calculoDaMedia();
            String situacao = avaliador.verificarAprovacao(media);

            System.out.printf("%-10s | %-12.1f | %-10s%n", aluno.getNome(), media, situacao);
            System.out.println("-----------|--------------|-----------");
        }
    }
}
