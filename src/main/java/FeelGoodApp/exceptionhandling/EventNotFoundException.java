package FeelGoodApp.exceptionhandling;

public class EventNotFoundException extends RuntimeException {
    private int idNotFound;

    public EventNotFoundException(int idNotFound) {
        this.idNotFound = idNotFound;
    }

    public int getIdNotFound() {
        return idNotFound;
    }
}
