package com.lucas.restgame.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class Entity {
    private String name;
    private int health;
    private int power;
    private int defense;

    public Entity() { }

    public Entity(String name, int health, int power, int defense) {
        this.setName(name);
        this.setHealth(health);
        this.setPower(power);
        this.setDefense(defense);
    }

    public String getName() {
        return this.name;
    }

    public int getHealth() {
        return this.health;
    }

    public int getPower() {
        return this.power;
    }

    public int getDefense() {
        return this.defense;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public boolean isDead() { return this.health == 0; }
}
