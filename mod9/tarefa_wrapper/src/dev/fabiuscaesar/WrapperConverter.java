package dev.fabiuscaesar;

/**
 * @author FabiusCaesar
 */
public class WrapperConverter {

    // Variável tipo primitivo
    private boolean isTaskDone = true;

    // Variável tipo wrapper
    private Boolean isDone = isTaskDone;

    // Getters
    public boolean getPrimitiveValue() {
        return isTaskDone;
    }

    public Boolean getWrapperValue() {
        return isDone;
    }
}
