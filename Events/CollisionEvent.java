package Events;

public class CollisionEvent extends AbstractEvent{
    private final int status;
    public CollisionEvent(Object source, int status) {
        super(source);
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
}
