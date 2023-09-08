package com.lucas.restgame.entity;

public class Player extends Entity {

    public Player() {
        this.setName("Player");
        this.setHealth(100);
        this.setPower(25);
        this.setDefense(5);
        this.setDodgeChance(0.5f);
    }

    public Player(
            String name,
            int health,
            int power,
            int defense,
            float dodgeChance) {
        super(name, health, power, defense, dodgeChance);
    }
}
