package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */
public class NotasEMedia {

    // Atributos
    private double[] notas; // Array de notas
    private  double mediaDeAprovacao;

    // Getters e Setters
    public double[] getNotas() {
        return notas;
    }

    public void setNotas(double[] notas) {
        for (double nota : notas) {
            if (nota < 0 || nota > 10) {
                throw new IllegalArgumentException("Nota inválida: " + nota + ". As notas devem estar entre 0 e 10.");
            }
        }
        this.notas = notas;
    }

    public double getMediaDeAprovacao() {
        return mediaDeAprovacao;
    }

    public void setMediaDeAprovacao(double mediaDeAprovacao) {
        this.mediaDeAprovacao = mediaDeAprovacao;
    }

    // Método para arredondamento da média para o múltiplo de 0.5 mais próximo
    private double arredondarMedia(double valor) {
        return Math.round(valor * 2) / 2.0;
    }

    // Método para calcular a média
    public double calculoDaMedia() {
        double soma = 0;
        for (double nota : notas) {
            soma += nota;
        }
        return arredondarMedia(soma / notas.length);
    }

    // Método para avaliação da média com base na média de aprovação
    public String verificarAprovacao(double media) {
        return (media >= mediaDeAprovacao) ? "Aprovado" : "Reprovado";
    }
}
