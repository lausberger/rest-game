package com.lucas.restgame.entity;

public class Goblin extends Enemy {

    private final float[] actionCDF = {0.5f, 0.3f, 0.2f, 0.0f};

    public Goblin() {
        this.setName("Goblin");
        this.setHealth(80);
        this.setPower(20);
        this.setDefense(5);
        this.setDodgeChance(0.5f);
    }
}
