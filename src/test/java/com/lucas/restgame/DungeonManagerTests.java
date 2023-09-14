package com.lucas.restgame;

import com.lucas.restgame.entity.Battle;
import com.lucas.restgame.entity.Dungeon;
import com.lucas.restgame.entity.DungeonInstance;
import com.lucas.restgame.entity.Room;
import com.lucas.restgame.model.BattleStatus;
import com.lucas.restgame.model.DungeonManager;
import com.lucas.restgame.model.DungeonStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class DungeonManagerTests {

    private String playerID;
    private Dungeon dungeon;
    private DungeonManager dungeonManager;

    @BeforeEach
    public void setup() {
        String playerID = "player";
        dungeon = new Dungeon();
        dungeonManager = new DungeonManager(dungeon);
    }

    @Test
    /*
    Given I have yet to start a Dungeon
    When I enter said Dungeon
    Then I should receive my own instance
    And I should be in the first Room
     */
    public void playerReceivesInstanceWhenEnteringDungeon() {
        Room firstRoom = dungeon.getRoom(0);

        dungeonManager.handleEnter(playerID);
        DungeonInstance dungeonInstance = dungeonManager.getDungeonInstance(playerID);

        assertEquals(playerID, dungeonInstance.getPlayerID());
        assertSame(firstRoom, dungeonInstance.getCurrentRoom());
    }

    @Test
    /*
    Given I have started a Dungeon
    When I leave the Dungeon before completing it
    Then my instance should be made inactive
     */
    public void playerLeavesDungeonInProgress() {
        dungeonManager.handleEnter(playerID);

        dungeonManager.handleExit(playerID);
        DungeonInstance dungeonInstance = dungeonManager.getDungeonInstance(playerID);

        // perhaps replace with dungeonManager interface method
        assertEquals(DungeonStatus.INACTIVE, dungeonInstance.getStatus());
    }

    @Test
    /*
    Given I have started a Dungeon
    And I have left the Dungeon before completing it
    When I re-enter the Dungeon
    Then my instance should be reactivated
    And I should be in the same Room as before
     */
    public void playerReEntersDungeonInProgress() {
        dungeonManager.handleEnter(playerID);
        DungeonInstance dungeonInstance = dungeonManager.getDungeonInstance(playerID);
        Room oldCurrentRoom = dungeonInstance.getCurrentRoom();
        dungeonManager.handleExit(playerID);

        dungeonManager.handleEnter(playerID);

        assertEquals(DungeonStatus.ACTIVE, dungeonInstance.getStatus());
        assertSame(oldCurrentRoom, dungeonInstance.getCurrentRoom());
    }

    @Test
    /*
    Given I am in the Room of a Dungeon instance
    When I win the Battle within the Room
    Then I should be able to move to the next Room
     */
    public void playerCanProgressAfterWinningBattle() {
        Battle battle = new Battle();
        battle.setStatus(BattleStatus.VICTORY);
        Room battleRoom = new Room(battle);
        Room nextRoom = new Room(battle); // replace with Treasure
        dungeon.setRooms(List.of(battleRoom, nextRoom));
        dungeonManager.handleEnter(playerID);

        dungeonManager.handleNextRoom(playerID);
        DungeonInstance dungeonInstance = dungeonManager.getDungeonInstance(playerID);

        assertSame(nextRoom, dungeonInstance.getCurrentRoom());
    }

    @Test
    /*
    Given I am in the Room of a dungeon
    And I have initiated Battle
    When I attempt to move to the next Room
    Then I should remain in my current Room
     */
    public void playerCannotProgressDuringBattle() {
        Battle battle = new Battle();
        battle.setStatus(BattleStatus.ONGOING);
        Room battleRoom = new Room(battle);
        Room nextRoom = new Room(battle);
        dungeon.setRooms(List.of(battleRoom, nextRoom));
        dungeonManager.handleEnter(playerID);

        dungeonManager.handleNextRoom(playerID);
        DungeonInstance dungeonInstance = dungeonManager.getDungeonInstance(playerID);

        assertSame(battleRoom, dungeonInstance.getCurrentRoom());
    }

    @Test
    /*
    Given I am in the Room of a Dungeon
    And I have initiated Battle
    When I attempt to move to the previous Room
    Then I should remain in my current Room
     */
    public void playerCannotGoBackDuringBattle() {
        Battle battle = new Battle();
        battle.setStatus(BattleStatus.ONGOING);
        Room battleRoom = new Room(battle);
        Room nextRoom = new Room(battle);
        dungeon.setRooms(List.of(battleRoom, nextRoom));
        dungeonManager.handleEnter(playerID);

        dungeonManager.handlePreviousRoom(playerID);
        DungeonInstance dungeonInstance = dungeonManager.getDungeonInstance(playerID);

        assertSame(battleRoom, dungeonInstance.getCurrentRoom());
    }

    @Test
    /*
    Given I am in a Dungeon instance
    When I enter a Room containing a Battle
    Then the Battle should not automatically start
     */
    public void battlesDoNotStartAutomatically() {
        Battle finishedBattle = new Battle();
        Battle nextBattle = new Battle();
        finishedBattle.setStatus(BattleStatus.VICTORY);
        nextBattle.setStatus(BattleStatus.NOT_STARTED);
        Room firstRoom = new Room(finishedBattle);
        Room secondRoom = new Room(nextBattle);
        dungeon.setRooms(List.of(firstRoom, secondRoom));
        dungeonManager.handleEnter(playerID);

        dungeonManager.handleNextRoom(playerID);
        Battle roomBattle = (Battle) dungeonManager
                .getDungeonInstance(playerID) // really?
                .getCurrentRoom().getContents();

        assertEquals(BattleStatus.NOT_STARTED, roomBattle.getStatus());
    }

    @Test
    /*
    Given I enter a Room containing a Battle
    When I choose to fight
    Then the Battle should be in progress
     */
    public void playerCanFightToStartBattle() {
        Battle finishedBattle = new Battle();
        Battle nextBattle = new Battle();
        finishedBattle.setStatus(BattleStatus.VICTORY);
        nextBattle.setStatus(BattleStatus.NOT_STARTED);
        Room firstRoom = new Room(finishedBattle);
        Room secondRoom = new Room(nextBattle);
        dungeon.setRooms(List.of(firstRoom, secondRoom));
        dungeonManager.handleEnter(playerID);

        dungeonManager.handleNextRoom(playerID);
        dungeonManager.handleInitiateCombat(playerID);
        Battle roomBattle = (Battle) dungeonManager
                .getDungeonInstance(playerID) // really?
                .getCurrentRoom().getContents();

        assertEquals(BattleStatus.ONGOING, roomBattle.getStatus());
    }

    @Test
    /*
    Given I enter a Room containing a Battle
    When I choose to run
    Then the Battle should not begin
    And I should go back to the previous Room
     */
    public void playerCanGoBackToAvoidBattle() {
        Battle finishedBattle = new Battle();
        Battle nextBattle = new Battle();
        finishedBattle.setStatus(BattleStatus.VICTORY);
        nextBattle.setStatus(BattleStatus.NOT_STARTED);
        Room firstRoom = new Room(finishedBattle);
        Room secondRoom = new Room(nextBattle);
        dungeon.setRooms(List.of(firstRoom, secondRoom));
        dungeonManager.handleEnter(playerID);

        dungeonManager.handleNextRoom(playerID);
        DungeonInstance dungeonInstance = dungeonManager.getDungeonInstance(playerID);
        Battle roomBattle = (Battle) dungeonInstance // really
                .getCurrentRoom().getContents();
        dungeonManager.handlePreviousRoom(playerID);

        assertEquals(BattleStatus.NOT_STARTED, roomBattle.getStatus());
        assertSame(firstRoom, dungeonInstance.getCurrentRoom());
    }

    @Test
    /*
    Given I am in a Dungeon instance
    When I lose a Battle
    Then I should be kicked out of the Dungeon
    And my Dungeon instance should no longer exist
     */
    public void playerDeathEndsDungeonInstance() {
        Battle lostBattle = new Battle();
        lostBattle.setStatus(BattleStatus.DEFEAT);
        Room room = new Room(lostBattle);
        dungeon.setRooms(List.of(room));
        dungeonManager.handleEnter(playerID);

        assertThrows(NoSuchElementException.class,
                () -> dungeonManager.getDungeonInstance(playerID));
    }
}
