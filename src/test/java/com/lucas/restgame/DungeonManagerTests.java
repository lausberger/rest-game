package com.lucas.restgame;

import com.lucas.restgame.entity.Dungeon;
import com.lucas.restgame.model.DungeonManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DungeonManagerTests {

    private Dungeon dungeon;
    private DungeonManager dungeonManager;

    @BeforeEach
    public void setup() {
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

    }

    @Test
    /*
    Given I have started a Dungeon
    When I leave the Dungeon and re-enter
    Then I should receive the same instance as before
     */
    public void playerCanLeaveAndReEnterDungeon() {

    }

    @Test
    /*
    Given I am in the Room of a Dungeon instance
    When I win the Battle within the Room
    Then I should have the option to move to the next Room
     */
    public void playerCanProgressAfterWinningBattle() {

    }

    @Test
    /*
    Given I am in the Room of a Dungeon Instance
    And I have yet to complete the Battle within it
    When I attempt to move to the next Room
    Then I should remain in my current Room
     */
    public void playerCannotProgressBeforeWinningBattle() {

    }

    @Test
    /*
    Given I am in a Dungeon instance
    When I enter a Room containing a Battle
    Then the Battle should not automatically start
     */
    public void battlesDoNotStartAutomatically() {

    }

    @Test
    /*
    Given I enter a Room containing a Battle
    When I choose to fight
    Then the Battle should begin
     */
    public void playerCanFightToStartBattle() {

    }

    @Test
    /*
    Given I enter a Room containing a Battle
    When I choose to run
    Then the Battle should not begin
    And I should go back to the previous Room
     */
    public void playerCanRunToAvoidBattle() {

    }

    @Test
    /*
    Given I am in a Dungeon instance
    When I lose a Battle
    Then I should be kicked out of the Dungeon
    And my Dungeon instance should no longer exist
     */
    public void playerDeathEndsDungeonInstance() {

    }
}
