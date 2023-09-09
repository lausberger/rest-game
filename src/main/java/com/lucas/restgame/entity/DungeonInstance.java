package com.lucas.restgame.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class DungeonInstance {

    private final String playerID;
    private Room currentRoom;

    public DungeonInstance(String playerID, Room startingRoom) {
        this.playerID = playerID;
        this.currentRoom = startingRoom;
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

    public Interactable getRoomContents() {
        return this.currentRoom.getContents();
    }
}
