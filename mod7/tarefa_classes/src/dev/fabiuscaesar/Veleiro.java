package dev.fabiuscaesar;

/**
 * Classe que representa um barco a vela com propriedades e comportamentos básicos.
 * Permite issar e recolher  a vela, navegar e ancorar.
 * @author FabiusCaesar
 */
public class Veleiro {

    private String nome;
    private int comprimento; // em pés
    private int capacidade; // número máximo de tripulantes
    private int tripulacao; // número atual de tripulantes
    private boolean velaIcada;
    private double velocidadeAtual;

    public void embarcar(int quantidade) {

        int vagasDisponiveis = getCapacidade() - getTripulacao();

        if (quantidade <= vagasDisponiveis) {
            setTripulacao(getTripulacao() + quantidade);

            if (quantidade == 1) {
                System.out.println("1 pessoa embarcou no veleiro.");
            } else {
                System.out.println(quantidade + " pessoas embarcaram no veleiro.");
            }
        } else {
            if (vagasDisponiveis == 0) {
                System.out.println("Capacidade máxima atingida. Ninguém mais pode embarcar.");
            } else if (vagasDisponiveis == 1) {
                System.out.println("Só há espaço para mais uma pessoa.");
            } else {
                System.out.println("Só há espaço para mais " + vagasDisponiveis + " pessoas.");
            }
        }

    }

    public void desembarcar(int quantidade) {
        if (quantidade <= getTripulacao()) {
            setTripulacao(getTripulacao() - quantidade);

            if (quantidade == 1) {
                System.out.println("1 pessoa desembarcou do veleiro.");
            } else {
                System.out.println(quantidade + " pessoas desembarcaram do veleiro.");
            }
        } else {
            if (getTripulacao() == 0) {
                System.out.println("Não há ninguém no veleiro para desembarcar.");
            } else if (getTripulacao() == 1) {
                System.out.println("Só há uma pessoa no veleiro para desembarcar.");
            } else {
                System.out.println("Só há " + getTripulacao() + " pessoas no veleiro para desembarcar.");
            }
        }
    }

    public void icarVela() {
        if (isVelaIcada()) {
            System.out.println("A vela já está içada.");
        } else {
            setVelaIcada(true);
            System.out.println("A vela foi içada.");
        }
    }

    public void recolherVela() {
        if (!isVelaIcada()) {
            System.out.println("A vela já está recolhida.");
        } else {
            setVelaIcada(false);
            System.out.println("A vela foi recolhida.");

            if (getVelocidadeAtual() > 0) {
                System.out.println("O veleiro está parando seu deslocamento.");
                setVelocidadeAtual(0);
            }
        }
    }

    public void navegar(double velocidade) {
        if (isVelaIcada()) {
            setVelocidadeAtual(velocidade);
            System.out.println("O veleiro está navegando a " + velocidade + " nós.");
        } else {
            System.out.println("Não é possível navegar com a vela recolhida.");
        }
    }

    public void ancorar() {
        System.out.println("A âncora foi lançada.");
        setVelocidadeAtual(0);
        System.out.println("O veleiro está ancorado.");
    }

    public void exibirStatus() {
        System.out.println("=========================");
        System.out.println("=== Status do Veleiro ===");
        System.out.println("Nome: " + getNome());
        System.out.println("Comprimento: " + getComprimento() + " pés");
        System.out.println("Tripulação: " + getTripulacao() + " tripulantes de " + getCapacidade());
        System.out.println("Vela: " + (isVelaIcada() ? "içada" : "recolhida"));
        System.out.println("Velocidade atual: " + getVelocidadeAtual() + " nós");
        System.out.println("=========================");
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getComprimento() {
        return comprimento;
    }

    public void setComprimento(int comprimento) {
        this.comprimento = comprimento;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public boolean isVelaIcada() {
        return velaIcada;
    }

    public void setVelaIcada(boolean velaIcada) {
        this.velaIcada = velaIcada;
    }

    public double getVelocidadeAtual() {
        return velocidadeAtual;
    }

    public void setVelocidadeAtual(double velocidadeAtual) {
        this.velocidadeAtual = velocidadeAtual;
    }

    public int getTripulacao() {
        return tripulacao;
    }

    public void setTripulacao(int tripulacao) {
        this.tripulacao = tripulacao;
    }
}
