package FeelGoodApp.exceptionhandling;

public class HasSamePerformerException extends RuntimeException {
    private int idFound;

    public HasSamePerformerException(int idFound) {
        this.idFound = idFound;
    }

    public int getIdFound() {
        return idFound;
    }
}
