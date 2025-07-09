package dev.fabiuscaesar.model;

/**
 * @author FabiusCaesar
 */
public class Moto extends Veiculo{
    private boolean partidaEletrica;
    private int cilindradas;

    public Moto(String modelo, String fabricante, String cor, int ano, boolean partidaEletrica, int cilindradas) {
        super(modelo, fabricante, cor, ano);
        this.partidaEletrica = partidaEletrica;
        this.cilindradas = cilindradas;
    }

    public boolean temPartidaEletrica() {
        return partidaEletrica;
    }

    public int getCilindradas() {
        return cilindradas;
    }

    @Override
    protected void exibirEspecificos() {
        System.out.println("Partida elétrica: " + (partidaEletrica ? "Sim" : "Não"));
        System.out.println("Cilindradas: " + cilindradas + "cc");
    }
}
