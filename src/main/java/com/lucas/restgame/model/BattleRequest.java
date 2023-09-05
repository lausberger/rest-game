package com.lucas.restgame.model;

import com.lucas.restgame.entity.Enemy;

public class BattleRequest {

    public BattleAction action;
    public Enemy target;

    public BattleAction getAction() {
        return action;
    }
    public void setAction(BattleAction action) {
        this.action = action;
    }

    public Enemy getTarget() {
        return target;
    }
    public void setTarget(Enemy target) {
        this.target = target;
    }
}
