package com.lucas.restgame.model;

import com.lucas.restgame.entity.Dungeon;
import com.lucas.restgame.entity.DungeonInstance;

public class DungeonManager {

    private final Dungeon dungeon;

    public DungeonManager(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void handleEnter(String playerID) {

    }

    public void handleExit(String playerID) {

    }

    public void handleNextRoom(String playerID) {

    }

    public void handlePreviousRoom(String playerID) {

    }

    public void handleInitiateCombat(String playerID) {

    }

    public DungeonInstance getDungeonInstance(String playerID) {
        return dungeon.getDungeonInstance(playerID);
    }
}
