package Events;

public class EatenFoodEvent extends AbstractEvent{
    private final int newScore;
    public EatenFoodEvent(Object source, int newScore) {
        super(source);
        this.newScore = newScore;
    }

    public int getNewScore(){
        return newScore;
    }
}
