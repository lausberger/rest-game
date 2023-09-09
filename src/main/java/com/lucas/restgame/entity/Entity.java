package com.lucas.restgame.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;

@DynamoDBDocument
public class Entity {
    private String name;
    private int health;
    private int power;
    private int defense;
    private float dodgeChance;

    public Entity() { }

    public Entity(String name, int health, int power, int defense, float dodgeChance) {
        this.setName(name);
        this.setHealth(health);
        this.setPower(power);
        this.setDefense(defense);
        this.setDodgeChance(dodgeChance);
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getHealth() {
        return this.health;
    }
    public void setHealth(int health) {
        this.health = health;
    }

    public int getPower() {
        return this.power;
    }
    public void setPower(int power) {
        this.power = power;
    }

    public int getDefense() {
        return this.defense;
    }
    public void setDefense(int defense) {
        this.defense = defense;
    }

    public float getDodgeChance() { return this.dodgeChance; }
    public void setDodgeChance(float dodgeChance) {
        this.dodgeChance = dodgeChance;
    }

    public boolean attemptDodge() {
        return Math.random() < dodgeChance;
    }

    @DynamoDBIgnore
    public boolean isDead() { return this.health == 0; }
}
