package dev.fabiuscaesar.abstractfactory.model;

/**
 * @author FabiusCaesar
 */public abstract class SUV extends Car {

    protected boolean tracaoIntegral; // atributo exclusivo dos SUVs

    public SUV() {
        this.categoria = "SUV"; // Define automaticamente
    }

    @Override
    public void exibirInfo() {
        super.exibirInfo();  // chama o método da classe Car
        System.out.println("Tração integral (4x4): " + (tracaoIntegral ? "Sim" : "Não"));
    }
}
