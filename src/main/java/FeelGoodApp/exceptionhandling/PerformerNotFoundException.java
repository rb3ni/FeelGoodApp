package FeelGoodApp.exceptionhandling;

public class PerformerNotFoundException extends RuntimeException {
    private int idNotFound;

    public PerformerNotFoundException(int idNotFound) {
        this.idNotFound = idNotFound;
    }

    public int getIdNotFound() {
        return idNotFound;
    }
}
