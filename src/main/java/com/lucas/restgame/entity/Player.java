package com.lucas.restgame.entity;

public class Player extends Entity {

    public Player() {
        this.setName("Player");
        this.setHealth(100);
        this.setPower(20);
        this.setDefense(10);
    }

    public Player(String name, int health, int power, int defense) {
        super(name, health, power, defense);
    }
}
