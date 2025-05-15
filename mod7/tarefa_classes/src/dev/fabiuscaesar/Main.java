package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */
public class Main {
    public static  void main(String[] args) {
        Veleiro veleiro = new Veleiro();

        // Configuração inicial
        veleiro.setNome("Lamúria das Águas");
        veleiro.setComprimento(40);
        veleiro.setCapacidade(8);

        veleiro.exibirStatus();

        // Primeiro ciclo
        veleiro.embarcar(9); // inválido
        veleiro.embarcar(5);
        veleiro.navegar(7.3); // sem vela
        veleiro.icarVela();
        veleiro.navegar(7.3);

        veleiro.exibirStatus();

        // Segundo ciclo
        veleiro.recolherVela();
        veleiro.ancorar();
        veleiro.desembarcar(6); // parcial
        veleiro.desembarcar(4); // excede
        veleiro.embarcar(2);
        veleiro.icarVela();
        veleiro.navegar(5.8);

        veleiro.exibirStatus();

        // Terceiro ciclo
        veleiro.recolherVela();
        veleiro.ancorar();
        veleiro.desembarcar(3);
        
        veleiro.exibirStatus();
    }
}
