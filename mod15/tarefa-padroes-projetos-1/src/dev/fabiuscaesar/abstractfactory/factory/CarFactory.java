package dev.fabiuscaesar.abstractfactory.factory;

import dev.fabiuscaesar.abstractfactory.model.SUV;
import dev.fabiuscaesar.abstractfactory.model.Sedan;

/**
 * @author FabiusCaesar
 */
public interface CarFactory {

    Sedan createSedan();
    SUV createSUV();
}
