package com.lucas.restgame;

import com.lucas.restgame.entity.Battle;
import com.lucas.restgame.entity.Enemy;
import com.lucas.restgame.entity.Player;
import com.lucas.restgame.model.BattleAction;
import com.lucas.restgame.model.BattleManager;
import com.lucas.restgame.model.BattleStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BattleManagerTests {

    private BattleManager battleManager = new BattleManager();

    private Battle battle;

    @BeforeEach
    public void setup() {
        Player player = new Player("Player", 100, 20, 10);
        Enemy enemy = new Enemy("Enemy", 100, 20, 10);
        battle = new Battle(player, enemy);
    }

    /*
     Stub the first Enemy to always perform a given BattleAction.
     TODO: support for specifying which enemy/enemies to stub
     */
    public void forceBattleAction(Battle battle, BattleAction action) {
        Enemy enemy = spy(battle.getEnemies().get(0));
        when(enemy.getAction()).thenReturn(action);
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(enemy);
        battle.setEnemies(enemies);
    }

    @Test
    /*
    Given player uses ATTACK
    When enemy uses ATTACK
    Then both should lose health
     */
    public void playerAttacksEnemyAttacks() {
        // stub getAction() to ATTACK
        forceBattleAction(battle, BattleAction.ATTACK);

        // starting health
        int enemyHealthBefore = battle.getEnemies().get(0).getHealth();
        int playerHealthBefore = battle.getPlayer().getHealth();
        battleManager.simulateBattle(battle, BattleAction.ATTACK);
        // ending health
        int enemyHealthAfter = battle.getEnemies().get(0).getHealth();
        int playerHealthAfter = battle.getPlayer().getHealth();

        assertTrue(enemyHealthBefore > enemyHealthAfter);
        assertTrue(playerHealthBefore > playerHealthAfter);
    }

    @Test
    /*
    Given player uses DEFEND
    When enemy uses ATTACK
    Then player should not take full damage
     */
    public void playerDefendsEnemyAttacks() {
        // stub getAction() to ATTACK
        forceBattleAction(battle, BattleAction.ATTACK);

        // full damage amount subtracted from player health
        Enemy enemy = battle.getEnemies().get(0);
        int undefendedHealth = battle.getPlayer().getHealth()
                - (enemy.getPower() - battle.getPlayer().getDefense());
        // get actual health after defending
        battleManager.simulateBattle(battle, BattleAction.DEFEND);
        int defendedHealth = battle.getPlayer().getHealth();

        assertTrue(defendedHealth > undefendedHealth);
    }

    @Test
    /*
    Given player uses ATTACK
    When enemy uses DEFEND
    Then enemy should not take full damage
     */
    public void playerAttacksEnemyDefends() {
        // stub getAction() to DEFEND
        forceBattleAction(battle, BattleAction.DEFEND);

        // full damage amount subtracted from enemy health
        Enemy enemy = battle.getEnemies().get(0);
        int undefendedHealth = enemy.getHealth()
                - (battle.getPlayer().getPower() - enemy.getDefense());

        // get actual health after defending
        battleManager.simulateBattle(battle, BattleAction.ATTACK);
        int defendedHealth = enemy.getHealth();

        assertTrue(defendedHealth > undefendedHealth);
    }

    @Test
    /*
    Given player uses DODGE
    When enemy uses DEFEND
    Then enemy should have priority
     */
    public void priorityWhenPlayerDodgesEnemyDefends() {
        // stub getAction() to DEFEND
        forceBattleAction(battle, BattleAction.DEFEND);

        battleManager.simulateBattle(battle, BattleAction.DODGE);

        assertEquals(1, battle.getPriority());
    }

    @Test
    /*
    Given player uses DEFEND
    When enemy uses DODGE
    Then player should have priority
     */
    public void priorityWhenPlayerDefendsEnemyDodges() {
        // stub getAction() to DODGE
        forceBattleAction(battle, BattleAction.DODGE);

        battleManager.simulateBattle(battle, BattleAction.DEFEND);

        assertEquals(0, battle.getPriority());
    }

    @Test
    /*
    Given player uses DODGE
    When enemy uses DODGE
    Then no damage should be done
    And priority should be unchanged
     */
    public void playerDodgesEnemyDodges() {
        // stub getAction() to DODGE
        forceBattleAction(battle, BattleAction.DODGE);

        Enemy enemy = battle.getEnemies().get(0);
        int startPlayerHealth = battle.getPlayer().getHealth();
        int startEnemyHealth = enemy.getHealth();
        battleManager.simulateBattle(battle, BattleAction.DODGE);
        int endPlayerHealth = battle.getPlayer().getHealth();
        int endEnemyHealth = enemy.getHealth();

        assertEquals(startPlayerHealth, endPlayerHealth);
        assertEquals(startEnemyHealth, endEnemyHealth);
        assertEquals(-1, battle.getPriority());
    }

    @Test
    /*
    Given there is one enemy with low health
    When player uses ATTACK
    Then enemy should be deleted
    And status should be VICTORY
     */
    public void playerVictory() {
        // stub getAction() to ATTACK
        forceBattleAction(battle, BattleAction.ATTACK);

        battle.getEnemies().get(0).setHealth(1);
        battleManager.simulateBattle(battle, BattleAction.ATTACK);

        assertTrue(battle.getEnemies().isEmpty());
        assertEquals(BattleStatus.VICTORY, battle.getStatus());
    }

    @Test
    /*
    Given player has low health
    When enemy uses ATTACK
    Then player should have 0 health
    And status should be DEFEAT
     */
    public void playerDefeat() {
        // stub getAction() to ATTACK
        forceBattleAction(battle, BattleAction.ATTACK);

        battle.getPlayer().setHealth(1);
        battleManager.simulateBattle(battle, BattleAction.ATTACK);

        assertEquals(0, battle.getPlayer().getHealth());
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
        // stub getAction() to ATTACK
        forceBattleAction(battle, BattleAction.ATTACK);

        Enemy enemy = battle.getEnemies().get(0);
        int enemyInitialHealth = enemy.getHealth();
        battle.setPriority(1);
        battle.getPlayer().setHealth(1);
        battleManager.simulateBattle(battle, BattleAction.ATTACK);

        assertEquals(enemy.getHealth(), enemyInitialHealth);
    }

    @Test
    /*
    Given player has attack priority
    And enemy has low health
    When player kills enemy
    Then enemy should not attack after
     */
    public void noEnemyAttackWhenEnemyDiesFirst() {
        // stub getAction() to ATTACK
        forceBattleAction(battle, BattleAction.ATTACK);

        int playerInitialHealth = battle.getPlayer().getHealth();
        battle.setPriority(0);
        battle.getEnemies().get(0).setHealth(1);
        battleManager.simulateBattle(battle, BattleAction.ATTACK);

        assertEquals(battle.getPlayer().getHealth(), playerInitialHealth);
    }

    @Test
    /*
    Given player uses ATTACK
    When enemy successfully uses DODGE
    Then enemy should take no damage
    And enemy should gain attack priority
     */
    public void playerAttackEnemyDodgeSuccessful() {
        // stub getAction() to DODGE
        forceBattleAction(battle, BattleAction.DODGE);
        // stub attemptDodge() to always succeed
        BattleManager stubBattleManager = spy(BattleManager.class);
        when(stubBattleManager.coinFlip(anyFloat())).thenReturn(true);

        int startingEnemyHealth = battle.getEnemies().get(0).getHealth();
        stubBattleManager.simulateBattle(battle, BattleAction.ATTACK);
        int endingEnemyHealth = battle.getEnemies().get(0).getHealth();

        assertEquals(startingEnemyHealth, endingEnemyHealth);
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
        // stub getAction() to DODGE
        forceBattleAction(battle, BattleAction.DODGE);
        // stub attemptDodge() to always succeed
        BattleManager stubBattleManager = spy(BattleManager.class);
        when(stubBattleManager.coinFlip(anyFloat())).thenReturn(false);

        int startingEnemyHealth = battle.getEnemies().get(0).getHealth();
        stubBattleManager.simulateBattle(battle, BattleAction.ATTACK);
        int endingEnemyHealth = battle.getEnemies().get(0).getHealth();

        assertTrue(startingEnemyHealth > endingEnemyHealth);
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
        // stub getAction() to ATTACK
        forceBattleAction(battle, BattleAction.ATTACK);
        // stub attemptDodge() to always succeed
        BattleManager stubBattleManager = spy(BattleManager.class);
        when(stubBattleManager.coinFlip(anyFloat())).thenReturn(true);

        int startingPlayerHealth = battle.getPlayer().getHealth();
        stubBattleManager.simulateBattle(battle, BattleAction.DODGE);
        int endingPlayerHealth = battle.getPlayer().getHealth();

        assertEquals(startingPlayerHealth, endingPlayerHealth);
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
        // stub getAction() to ATTACK
        forceBattleAction(battle, BattleAction.ATTACK);
        // stub attemptDodge() to always succeed
        BattleManager stubBattleManager = spy(BattleManager.class);
        when(stubBattleManager.coinFlip(anyFloat())).thenReturn(false);

        int startingPlayerHealth = battle.getPlayer().getHealth();
        stubBattleManager.simulateBattle(battle, BattleAction.DODGE);
        int endingPlayerHealth = battle.getPlayer().getHealth();

        assertTrue(startingPlayerHealth > endingPlayerHealth);
        assertEquals(-1, battle.getPriority());
    }
}
