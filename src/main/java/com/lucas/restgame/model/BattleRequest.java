package com.lucas.restgame.model;

import com.lucas.restgame.entity.Enemy;

public class BattleRequest {

    public BattleAction action;
    public int target;

    public BattleAction getAction() {
        return action;
    }
    public void setAction(BattleAction action) {
        this.action = action;
    }

    public int getTarget() {
        return target;
    }
    public void setTarget(int target) {
        this.target = target;
    }
}
