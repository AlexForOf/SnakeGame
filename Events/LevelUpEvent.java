package Events;

public class LevelUpEvent extends AbstractEvent{
    private final int newLevel;
    public LevelUpEvent(Object source, int newLevel) {
        super(source);
        this.newLevel = newLevel;
    }

    public int getNewLevel(){
        return this.newLevel;
    }
}
