package Events;

public class AbstractEvent {
    protected Object source;

    public AbstractEvent(Object source) {
        this.source = source;
    }
}
