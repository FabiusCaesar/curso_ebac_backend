package dev.fabiuscaesar.abstractfactory.factory;

import dev.fabiuscaesar.abstractfactory.model.*;

/**
 * @author FabiusCaesar
 */
public class ToyotaFactory implements CarFactory{

    @Override
    public Sedan createSedan() {
        return new ToyotaSedan();
    }

    @Override
    public SUV createSUV() {
        return new ToyotaSUV();
    }
}
