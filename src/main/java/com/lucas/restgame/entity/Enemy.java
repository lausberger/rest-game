package com.lucas.restgame.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.lucas.restgame.model.BattleAction;

public class Enemy extends Entity {
    private final float[] actionCDF = {0.25f, 0.25f, 0.25f, 0.25f};

    public Enemy() {
        this.setName("Enemy");
        this.setHealth(100);
        this.setPower(20);
        this.setDefense(10);
    }

    public Enemy(String name, int health, int power, int defense) {
        super(name, health, power, defense);
    }

    @DynamoDBIgnore
    public BattleAction battleAction() {
        double value = Math.random();
        float cur = 0;
        int i;
        for (i = 0; i < actionCDF.length; i++) {
            cur += actionCDF[i];
            if (value < cur) {
                break;
            }
        }
        return BattleAction.values()[i];
    }
}
