package FeelGoodApp.exceptionhandling;

public class MusicVenueNotFoundException extends RuntimeException {
    private int idNotFound;

    public MusicVenueNotFoundException(int idNotFound) {
        this.idNotFound = idNotFound;
    }

    public int getIdNotFound() {
        return idNotFound;
    }
}
