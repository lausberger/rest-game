package com.lucas.restgame.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.lucas.restgame.model.BattleStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@DynamoDBTable(tableName = "battles")
public class Battle implements Interactable {

    private String battleID;
    private Player player;
    private List<Enemy> enemies;
    private BattleStatus status;
    private int priority;
    private String text;

    // temporary constructors for debug purposes
    public Battle(Player player, Enemy enemy) {
        this.player = player;
        this.enemies = new ArrayList<>();
        this.enemies.add(enemy);
        this.status = BattleStatus.ONGOING;
        this.priority = -1;
        this.text = "";
    }

    public Battle(Player player, List<Enemy> enemies) {
        this.player = player;
        this.enemies = enemies;
        this.status = BattleStatus.ONGOING;
        this.priority = -1;
        this.text = "";
    }

    public Battle() {
        this.player = new Player();
        this.enemies = new ArrayList<>();
        this.enemies.add(new Enemy());
        this.status = BattleStatus.ONGOING;
        this.priority = -1;
        this.text = "";
    }

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    public String getBattleID() {
        return battleID;
    }

    @DynamoDBAttribute
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.M)
    public Player getPlayer() {
        return this.player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    @DynamoDBAttribute
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.L)
    public List<Enemy> getEnemies() { return this.enemies; }
    public void setEnemies(List<Enemy> enemies) { this.enemies = enemies; }

    @DynamoDBAttribute
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    public BattleStatus getStatus() { return this.status; }
    public void setStatus(BattleStatus status) { this.status = status; }

    @DynamoDBAttribute
    public int getPriority() { return this.priority; }
    public void setPriority(int priority) throws IllegalArgumentException {
        if (priority > 1 || priority < -1) {
            throw new IllegalArgumentException("Valid values: -1, 0, 1");
        }
        this.priority = priority;
    }

    @DynamoDBAttribute
    public String getText() { return this.text; }
    public void setText(String text) { this.text = text; }

    public void resetText() {
        this.text = "";
    }

    public void addText(String text) {
        if (this.text.isEmpty()) {
            this.text = text;
        } else {
            this.text += "/n" + text;
        }
    }

    @DynamoDBIgnore
    public Enemy getEnemy(int index) {
        return this.enemies.get(index);
    }

    public void addEnemy(Enemy enemy) throws IllegalArgumentException {
        if (this.enemies.contains(enemy)) {
            throw new IllegalArgumentException("Enemy already exists here.");
        }
        this.enemies.add(enemy);
    }

    public void removeEnemy(Enemy enemy) throws NoSuchElementException {
        boolean success = this.enemies.remove(enemy);
        if (!success) {
            throw new NoSuchElementException();
        }
    }

    public void addEnemies(Enemy... enemies) {
        for (Enemy e : enemies) {
            addEnemy(e);
        }
    }

    public void replaceEnemies(Enemy... enemies) {
        List<Enemy> newEnemies = new ArrayList<>(List.of(enemies));
        this.setEnemies(newEnemies);
    }

}
