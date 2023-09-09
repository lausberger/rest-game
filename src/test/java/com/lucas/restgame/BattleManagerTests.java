package com.lucas.restgame;

import com.lucas.restgame.entity.Battle;
import com.lucas.restgame.entity.Enemy;
import com.lucas.restgame.entity.Player;
import com.lucas.restgame.model.BattleAction;
import com.lucas.restgame.model.BattleManager;
import com.lucas.restgame.model.BattleStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BattleManagerTests {

    private Enemy enemyStub;
    private Battle battle;
    private BattleManager battleManager;

    @BeforeEach
    public void setup() {
        Player player = new Player("Player", 100, 20, 10, 0.5f);
        enemyStub = spy(new Enemy("Enemy", 100, 20, 10, 0.5f));
        battle = new Battle(player, enemyStub);
        battleManager = new BattleManager(battle);
    }

    public void enemyStubBattleAction(BattleAction action) {
        when(enemyStub.battleAction()).thenReturn(action);
    }
    public void enemyStubDodgeOutcome(boolean outcome) {
        when(enemyStub.attemptDodge()).thenReturn(outcome);
    }
    public void forcePlayerDodgeOutcome(boolean outcome) {
        Player player = spy(battle.getPlayer());
        when(player.attemptDodge()).thenReturn(outcome);
        battle.setPlayer(player);
    }

    @Test
    /*
    Given a combatant has priority
    When the next turn ends
    And neither combatant gains priority
    Then priority should reset to neutral
     */
    public void priorityResetsBetweenTurns() {
        enemyStubBattleAction(BattleAction.ATTACK);

        battle.setPriority(1);
        battleManager.performTurn(BattleAction.ATTACK);

        assertEquals(-1, battle.getPriority());
    }

    @Test
    /*
    Given player uses ATTACK
    When enemy uses ATTACK
    Then both should lose health
     */
    public void playerAttacksEnemyAttacks() {
        // stub battleAction() to ATTACK
        enemyStubBattleAction(BattleAction.ATTACK);

        // starting health
        Player player = battle.getPlayer();
        int enemyHealthBefore = enemyStub.getHealth();
        int playerHealthBefore = player.getHealth();
        battleManager.performTurn(BattleAction.ATTACK);

        assertTrue(enemyHealthBefore > enemyStub.getHealth());
        assertTrue(playerHealthBefore > player.getHealth());
    }

    @Test
    /*
    Given player uses DEFEND
    When enemy uses ATTACK
    Then player should not take full damage
     */
    public void playerDefendsEnemyAttacks() {
        // stub battleAction() to ATTACK
        enemyStubBattleAction(BattleAction.ATTACK);

        Player player = battle.getPlayer();
        // full damage amount subtracted from player health
        // not a good test, requires too much implementation knowledge!
        int undefendedHealth = player.getHealth()
                - (enemyStub.getPower() - player.getDefense());
        // get actual health after defending
        battleManager.performTurn(BattleAction.DEFEND);

        assertTrue(player.getHealth() > undefendedHealth);
    }

    @Test
    /*
    Given player uses ATTACK
    When enemy uses DEFEND
    Then enemy should not take full damage
     */
    public void playerAttacksEnemyDefends() {
        // stub battleAction() to DEFEND
        enemyStubBattleAction(BattleAction.DEFEND);

        Player player = battle.getPlayer();
        // full damage amount subtracted from enemy health
        // not a good test, requires too much implementation knowledge!
        int undefendedHealth = enemyStub.getHealth()
                - (player.getPower() - enemyStub.getDefense());

        // get actual health after defending
        battleManager.performTurn(BattleAction.ATTACK);

        assertTrue(enemyStub.getHealth() > undefendedHealth);
    }

    @Test
    /*
    Given player uses DODGE
    When enemy uses DEFEND
    Then enemy should have priority
     */
    public void priorityWhenPlayerDodgesEnemyDefends() {
        // stub battleAction() to DEFEND
        enemyStubBattleAction(BattleAction.DEFEND);

        battleManager.performTurn(BattleAction.DODGE);

        assertEquals(1, battle.getPriority());
    }

    @Test
    /*
    Given player uses DEFEND
    When enemy uses DODGE
    Then player should have priority
     */
    public void priorityWhenPlayerDefendsEnemyDodges() {
        // stub battleAction() to DODGE
        enemyStubBattleAction(BattleAction.DODGE);

        battleManager.performTurn(BattleAction.DEFEND);

        assertEquals(0, battle.getPriority());
    }

    @Test
    /*
    Given player uses DODGE
    When enemy uses DODGE
    Then no damage should be done
     */
    // this needs to be rewritten since dodge change in d20a971
    public void playerDodgesEnemyDodges() {
        // stub battleAction() to DODGE
        enemyStubBattleAction(BattleAction.DODGE);

        Player player = battle.getPlayer();
        int startPlayerHealth = player.getHealth();
        int startEnemyHealth = enemyStub.getHealth();
        battleManager.performTurn(BattleAction.DODGE);

        assertEquals(startPlayerHealth, player.getHealth());
        assertEquals(startEnemyHealth, enemyStub.getHealth());
//        assertEquals(-1, battle.getPriority());
    }

    @Test
    /*
    Given both combatants attempt to dodge
    When both succeed
    Then priority should remain neutral
     */
    public void neutralPriorityWhenBothDodgeSuccess() {
        enemyStubBattleAction(BattleAction.DODGE);
        enemyStubDodgeOutcome(true);
        forcePlayerDodgeOutcome(true);

        battleManager.performTurn(BattleAction.DODGE);

        assertEquals(-1, battle.getPriority());
    }

    @Test
    /*
    Given both combatants attempt to dodge
    When both fail
    Then priority should remain neutral
     */
    public void neutralPriorityWhenBothDodgeFailure() {
        enemyStubBattleAction(BattleAction.DODGE);
        enemyStubDodgeOutcome(false);
        forcePlayerDodgeOutcome(false);

        battleManager.performTurn(BattleAction.DODGE);

        assertEquals(-1, battle.getPriority());
    }

    @Test
    /*
    Given both combatants DODGE
    When only Player succeeds
    Then Player should gain priority
     */
    public void priorityWhenPlayerDodgeSuccessEnemyDodgeFail() {
        enemyStubBattleAction(BattleAction.DODGE);
        enemyStubDodgeOutcome(false);
        forcePlayerDodgeOutcome(true);

        battleManager.performTurn(BattleAction.DODGE);

        assertEquals(0, battle.getPriority());
    }

    @Test
    /*
    Given both combatants DODGE
    When only Player succeeds
    Then Player should gain priority
     */
    public void priorityWhenPlayerDodgeFailEnemyDodgeSucceed() {
        enemyStubBattleAction(BattleAction.DODGE);
        enemyStubDodgeOutcome(true);
        forcePlayerDodgeOutcome(false);

        battleManager.performTurn(BattleAction.DODGE);

        assertEquals(1, battle.getPriority());
    }

    @Test
    /*
    Given there is a single enemy remaining
    When player kills the enemy
    Then the list of enemies should be empty
    And status should be VICTORY
     */
    public void battleStatusWhenPlayerWins() {
        // stub battleAction() to ATTACK
        enemyStubBattleAction(BattleAction.ATTACK);

        enemyStub.setHealth(1);
        battleManager.performTurn(BattleAction.ATTACK);

        assertTrue(battle.getEnemies().isEmpty());
        assertEquals(BattleStatus.VICTORY, battle.getStatus());
    }

    @Test
    /*
    Given player has low health
    When an enemy kills the player
    Then player should have 0 health
    And status should be DEFEAT
     */
    public void battleStatusWhenPlayerDies() {
        // stub battleAction() to ATTACK
        enemyStubBattleAction(BattleAction.ATTACK);

        Player player = battle.getPlayer();
        player.setHealth(1);
        battleManager.performTurn(BattleAction.ATTACK);

        assertEquals(0, player.getHealth());
        assertEquals(BattleStatus.DEFEAT, battle.getStatus());
    }

    @Test
    /*
    Given enemy has attack priority
    And player has low health
    When enemy kills player
    Then player should not attack after
     */
    public void noPlayerAttackWhenPlayerDiesFirst() {
        // stub battleAction() to ATTACK
        enemyStubBattleAction(BattleAction.ATTACK);

        Player player = battle.getPlayer();
        int enemyInitialHealth = enemyStub.getHealth();
        battle.setPriority(1);
        player.setHealth(1);
        battleManager.performTurn(BattleAction.ATTACK);

        assertEquals(enemyInitialHealth, enemyStub.getHealth());
    }

    @Test
    /*
    Given player has attack priority
    And enemy has low health
    When player kills enemy
    Then enemy should not attack after
     */
    public void noEnemyAttackWhenEnemyDiesFirst() {
        // stub battleAction() to ATTACK
        enemyStubBattleAction(BattleAction.ATTACK);

        Player player = battle.getPlayer();
        int playerInitialHealth = player.getHealth();
        battle.setPriority(0);
        enemyStub.setHealth(1);
        battleManager.performTurn(BattleAction.ATTACK);

        assertEquals(playerInitialHealth, player.getHealth());
    }

    @Test
    /*
    Given player uses ATTACK
    When enemy successfully uses DODGE
    Then enemy should take no damage
    And enemy should gain attack priority
     */
    public void playerAttackEnemyDodgeSuccess() {
        // stub battleAction() to DODGE
        enemyStubBattleAction(BattleAction.DODGE);
        // stub Enemy dodge() to always succeed
        enemyStubDodgeOutcome(true);

        int startingEnemyHealth = enemyStub.getHealth();
        battleManager.performTurn(BattleAction.ATTACK);

        assertEquals(startingEnemyHealth, enemyStub.getHealth());
        assertEquals(1, battle.getPriority());
    }

    @Test
    /*
    Given player uses ATTACK
    When enemy fails to DODGE
    Then enemy should take full damage
    And priority should be neutral
     */
    public void playerAttackEnemyDodgeFailure() {
        // stub battleAction() to DODGE
        enemyStubBattleAction(BattleAction.DODGE);
        // stub Enemy dodge() to always fail
        enemyStubDodgeOutcome(false);

        int startingEnemyHealth = enemyStub.getHealth();
        battleManager.performTurn(BattleAction.ATTACK);

        assertTrue(enemyStub.getHealth() < startingEnemyHealth);
        assertEquals(-1, battle.getPriority());
    }

    @Test
    /*
    Given player uses DODGE successfully
    When enemy uses ATTACK
    Then player should take no damage
    And player should gain attack priority
     */
    public void playerDodgeSuccessEnemyAttack() {
        // stub Enemy to ATTACK
        enemyStubBattleAction(BattleAction.ATTACK);
        // stub Player dodge() to always succeed
        forcePlayerDodgeOutcome(true);

        int startingPlayerHealth = battle.getPlayer().getHealth();
        battleManager.performTurn(BattleAction.DODGE);

        assertEquals(startingPlayerHealth, battle.getPlayer().getHealth());
        assertEquals(0, battle.getPriority());
    }

    @Test
    /*
    Given enemy uses ATTACK
    When player fails to DODGE
    Then player should take full damage
    And priority should be neutral
     */
    public void playerDodgeFailureEnemyAttack() {
        // stub Enemy to ATTACK
        enemyStubBattleAction(BattleAction.ATTACK);
        // stub Player dodge() to always fail
        forcePlayerDodgeOutcome(false);

        Player player = battle.getPlayer();
        int startingPlayerHealth = player.getHealth();
        battleManager.performTurn(BattleAction.DODGE);

        assertTrue(player.getHealth() < startingPlayerHealth);
        assertEquals(-1, battle.getPriority());
    }
}
