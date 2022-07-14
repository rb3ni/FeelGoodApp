package FeelGoodApp.exceptionhandling;

public class EventAlreadyHasHeadlinerException extends RuntimeException {
    private int idFound;

    public EventAlreadyHasHeadlinerException(int idFound) {
        this.idFound = idFound;
    }

    public int getIdFound() {
        return idFound;
    }
}
