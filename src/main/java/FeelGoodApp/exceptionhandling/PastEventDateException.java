package FeelGoodApp.exceptionhandling;

public class PastEventDateException extends RuntimeException {
    private int idFound;

    public PastEventDateException(int idFound) {
        this.idFound = idFound;
    }

    public int getIdFound() {
        return idFound;
    }
}
