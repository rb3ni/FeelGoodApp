package FeelGoodApp.exceptionhandling;

public class HeadlinerRemoveException extends RuntimeException {
    private int eventId;
    private int performerId;


    public HeadlinerRemoveException(int eventId, int performerId) {
        this.eventId = eventId;
        this.performerId = performerId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getPerformerId() {
        return performerId;
    }
}
