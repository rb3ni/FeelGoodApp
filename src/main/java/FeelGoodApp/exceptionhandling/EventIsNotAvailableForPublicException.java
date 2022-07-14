package FeelGoodApp.exceptionhandling;

public class EventIsNotAvailableForPublicException extends RuntimeException {
    private int idFound;

    public EventIsNotAvailableForPublicException(int idFound) {
        this.idFound = idFound;
    }

    public int getIdFound() {
        return idFound;
    }
}
