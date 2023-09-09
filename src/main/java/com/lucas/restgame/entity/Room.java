package com.lucas.restgame.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class Room {

    private String description;
    private Interactable contents;

    public Room(String description, Interactable contents) {
        this.description = description;
        this.contents = contents;
    }

    public Room(Interactable contents) {
        this.description = "";
        this.contents = contents;
    }

    public Interactable getContents() {
        return contents;
    }
    public void setContents(Interactable contents) {
        this.contents = contents;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
