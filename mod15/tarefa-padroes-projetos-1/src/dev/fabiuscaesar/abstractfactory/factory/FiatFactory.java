package dev.fabiuscaesar.abstractfactory.factory;

import dev.fabiuscaesar.abstractfactory.model.*;

/**
 * @author FabiusCaesar
 */
public class FiatFactory implements CarFactory{

    @Override
    public Sedan createSedan() {
        return new FiatSedan();
    }

    @Override
    public SUV createSUV() {
        return new FiatSUV();
    }
}
