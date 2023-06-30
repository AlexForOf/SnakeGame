package Events;

public class DirectionChangedEvent extends AbstractEvent{
    private int direction;
    public DirectionChangedEvent(Object source){
        super(source);
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction){
        this.direction = direction;
    }

}
