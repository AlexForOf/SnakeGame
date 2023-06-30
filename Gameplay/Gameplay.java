package Gameplay;

import Events.*;

import java.util.Arrays;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.Random;

public class Gameplay implements Runnable, Connector, DirectionChangedEventListener, RestartEventListener {
    private int[][] field;

    private LinkedList<BoardElement> snakeBody;
    private BoardElement foodCell;

    private boolean eatenFood;
    private boolean gameOver;
    private boolean finished;
    private boolean changedDirection;

    private int direction;
    private int delay;
    private int score = 0;
    private int scoreNow = 0;
    private int level;

//    Auxiliary int arrays for defining proper position faster for tail and head
    private final int[] HEAD_POSITIONS = {4, 5, 6, 3};
    private int currentTailDirection = 0;
    private int newTailDirection = 0;
//    private final int[] ROTATIONS_POSITIONS = {}

    private MovementEventListener movementEventListener;
    private CollisionEventListener collisionEventListener;
    private EatenFoodEventListener eatenFoodEventListener;
    private LevelUpEventListener levelUpEventListener;

    public Gameplay(){
        this.field = new int[25][16];
        snakeBody = new LinkedList<>();
        start();
    }

    public void start(){
        field = new int[25][16];

        snakeBody = new LinkedList<>();
        snakeBody.add(new BoardElement(0, 2, 5));
        snakeBody.add(new BoardElement(0, 1, 1));
        snakeBody.add(new BoardElement(0, 0, 1));
        direction = -1;
        score = 0;
        scoreNow = 0;
        delay = 750;
        level = 1;
        gameOver = false;
        finished = false;
        changedDirection = false;

        placeFood();
        eatenFood = false;

        updateField();
        displayField();

        System.out.println();
        move();
    }

    private void move(){
        BoardElement head = snakeBody.getFirst();
        int newHeadX = head.getX();
        int newHeadY = head.getY();

        switch (direction) {
            case 0:
                newHeadX--;
                break;
            case 1:
                newHeadY++;
                break;
            case 2:
                newHeadX++;
                break;
            case 3:
                newHeadY--;
                break;
            default:
                return;
        }
        if (newHeadY < 0 || newHeadY >= field[0].length || newHeadX < 0 || newHeadX >= field.length) {
            collision(1);
            return;
        }
        if (occupiedByBody(newHeadX, newHeadY)) {
            collision(2);
            return;
        }

        if (newHeadX == foodCell.getX() && newHeadY == foodCell.getY()) eatFood();
        else eatenFood = false;

        if (!eatenFood) {
            field[snakeBody.getLast().getX()][snakeBody.getLast().getY()] = 0;
        }

        int bodypart = 0;
        if (direction == 0 || direction == 2){
            bodypart = 2;
        }else{
            bodypart = 1;
        }

        snakeBody.addFirst(new BoardElement(newHeadX, newHeadY, bodypart));

        if (!eatenFood) {
            snakeBody.removeLast();
        }

        updateField();
        displayField();
        if (scoreNow == level*5 && scoreNow != 0 && level < 19) {
            levelUp();
        };
        changedDirection = false;
    }

    public void updateField(){
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j] = 0;
                if (i == foodCell.getX() && j == foodCell.getY()){
                    field[i][j] = 15;
                }
            }
        }
        for (BoardElement segment: snakeBody) {
            if (segment == snakeBody.getFirst()){
                if (direction == -1){
                    field[segment.getX()][segment.getY()] = 5;
                }else{
                    field[segment.getX()][segment.getY()] = HEAD_POSITIONS[direction];
                }
            }else if(segment == snakeBody.getLast()){
                if (direction == -1){
                    field[segment.getX()][segment.getY()] = 13;
                    newTailDirection = 13;
                }else{
                    if (eatenFood){
                        field[segment.getX()][segment.getY()] = currentTailDirection;
                    }else{
                        currentTailDirection = newTailDirection;
                        int next = snakeBody.get(snakeBody.indexOf(segment) - 1).getType();
                        newTailDirection = defineTailDirection(currentTailDirection, next);
                        field[segment.getX()][segment.getY()] = currentTailDirection;
                    }
                }
            } else{
                field[segment.getX()][segment.getY()] = segment.getType();
            }
        }
    }

    private void levelUp(){
        scoreNow = 0;
        level++;

        LevelUpEvent levelUpEvent = new LevelUpEvent(this, level);
        levelUpEventListener.increaseLevel(levelUpEvent);
    }
    private void collision(int status){
        switchGameOver(true);

        CollisionEvent finishGame = new CollisionEvent(this, status);
        collisionEventListener.collision(finishGame);
    }
    private void eatFood(){
        eatenFood = true;
        score++;
        scoreNow++;

        EatenFoodEvent eatenFoodEvent = new EatenFoodEvent(this, score);
        eatenFoodEventListener.foodEaten(eatenFoodEvent);

        placeFood();
    }

    public void displayField(){
        if (movementEventListener != null){
            MovementEvent changedOnMovement = new MovementEvent(this);
            movementEventListener.updateOnMovement(changedOnMovement);
        }
    }
    public void placeFood(){
        Random random = new Random();
        int x = random.nextInt(field.length);
        int y = random.nextInt(field[0].length);
        while (occupiedByBody(x, y)){
            x = random.nextInt(field.length);
            y = random.nextInt(field[0].length);
        }
        field[x][y] = 5;
        foodCell = new BoardElement(x, y, 5);
    }

    private boolean occupiedByBody(int x, int y){
        for (BoardElement segment : snakeBody) {
            if (segment.getX() == x && segment.getY() == y) return true;
        }
        return false;
    }



    public void addEatenFoodEventListener(EatenFoodEventListener el){
        this.eatenFoodEventListener = el;
    }
    public void addCollisionEventListener(CollisionEventListener el){
        this.collisionEventListener = el;
    }
    public void addMovementEventListener(MovementEventListener el){
        this.movementEventListener = el;
    }
    public void addLevelUpEventListener(LevelUpEventListener el){
        this.levelUpEventListener = el;
    }

    @Override
    public void run() {
        while (!finished){
            try {
                synchronized (this){
                    while (gameOver){
                        wait();
                    }
                }
                move();
                try {
                Thread.sleep(delay - (level * 25));
//                Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    public synchronized void switchGameOver(boolean toSwitch) {
        this.gameOver = toSwitch;
        System.out.println(gameOver);
        if (!gameOver){
            notify();
        }
    }

    private int oppositeDirection(){
        switch (direction){
            case 0:
                return 2;
            case 1:
                return 3;
            case 2:
                return 0;
            case 3:
                return 1;
            default:
                return -1;
        }
    }
    private int defineTailDirection(int currentTail, int next){
        int type = 0;
        if(next == 1 || next == 2){
            type = currentTail;
        }else{
            switch (currentTail){
                case 12: // Up
                    type = ((next == 7) ? 11 : 13);
                    break;
                case 13: // Right
                    type = ((next == 9) ? 12 : 14);
                    break;
                case 14: // Down
                    type = ((next == 9) ? 11 : 13);
                    break;
                case 11: // Left
                    type = ((next == 8) ? 14 : 12);
                    break;
            }
        }
        return type;
    }
    private int defineRotationDirection(int previousRotation, int newRotation){
        return switch (previousRotation) {
            case 0 ->//Up
                    ((newRotation == 1) ? 8 : 7);
            case 1 ->//Right
                    ((newRotation == 0) ? 9 : 7);
            case 2 ->//Down
                    ((newRotation == 1) ? 10 : 9);
            case 3 ->//Left
                    ((newRotation == 0) ? 10 : 8);
            default -> ((newRotation == 2) ? 7 : 1);
        };
    }
    @Override
    public int[][] getField() {
        return field;
    }

    @Override
    public void changeDirection(DirectionChangedEvent e) {
        if (e.getDirection() != oppositeDirection() && e.getDirection() != direction && !changedDirection){
            snakeBody.get(0).setType(defineRotationDirection(direction, e.getDirection()));
            direction = e.getDirection();
            changedDirection = true;
        }
    }
    @Override
    public void restartGame(RestartEvent e) {
        switchGameOver(false);
        start();
        levelUpEventListener.increaseLevel(new LevelUpEvent(this, level));
        eatenFoodEventListener.foodEaten(new EatenFoodEvent(this, score));
    }
}
