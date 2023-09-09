package com.lucas.restgame.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/*
Here are my options for modeling this:

1. Dungeon --E DungeonInstance --E Room -- Battle --E BattleInstance
2. Dungeon --E (Player, Room -- Battle --E BattleInstance)
3. Dungeon --E (DungeonInstance -- Player -- BattleInstance, Room -- Battle)
4. Dungeon --E (Room -- Battle, DungeonInstance -- (Player, CurrentRoom))

Option 4

Dungeon
    Name
    Rooms {
        Floor1
            Battle
                Player
                Enemy
                Status
        Floor2
            Battle
        Floor3
            Treasure
    }
    DungeonInstances {
        Instance1
            PlayerID1
            CurrentRoom
        Instance2
            PlayerID2
            CurrentRoom
    }

 */

@DynamoDBTable(tableName = "dungeons")
public class Dungeon {
    private String name;
    private List<Room> rooms;
    private Map<String, DungeonInstance> dungeonInstances;

    @DynamoDBHashKey
    public String getName() { return this.name; }

    @DynamoDBAttribute
    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }

    @DynamoDBAttribute
    public Map<String, DungeonInstance> getInstances() {
        return dungeonInstances;
    }

    public Room getRoom(int index) { return rooms.get(index); }

    public void addDungeonInstance(DungeonInstance dungeonInstance) {
        this.dungeonInstances.put(
                dungeonInstance.getPlayerID(), dungeonInstance);
    }

    public void removeDungeonInstance(String playerID)
            throws NoSuchElementException {
        DungeonInstance value =
                this.dungeonInstances.remove(playerID);
        if (value == null) {
            throw new NoSuchElementException(
                    "Dungeon instance for " + playerID + " does not exist.");
        }

    }
}
