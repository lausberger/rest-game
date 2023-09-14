package com.lucas.restgame.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.lucas.restgame.model.DungeonStatus;

@DynamoDBDocument
public class DungeonInstance {

    private final String playerID;
    private Room currentRoom;

    private DungeonStatus status;

    public DungeonInstance(String playerID, Room startingRoom) {
        this.playerID = playerID;
        this.currentRoom = startingRoom;
        this.status = DungeonStatus.ACTIVE;
    }

    public String getPlayerID() {
        return playerID;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public DungeonStatus getStatus() {
        return status;
    }
    public void setStatus(DungeonStatus status) {
        this.status = status;
    }

    public Interactable getRoomContents() {
        return this.currentRoom.getContents();
    }
}
