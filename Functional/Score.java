package Functional;

public class Score implements Comparable<Score>{
    private String player;
    private int score;

    public Score(String player, int score) {
        this.player = player;
        this.score = score;
    }

    public String getPlayer() {
        return this.player;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(Score score) {
        return Integer.compare(score.score, this.score);
    }

    @Override
    public String toString() {
        return player + " with score: " + score;
    }
}
