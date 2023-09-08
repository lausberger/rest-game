package com.lucas.restgame;

import com.lucas.restgame.entity.Battle;
import com.lucas.restgame.entity.Enemy;
import com.lucas.restgame.entity.Player;
import com.lucas.restgame.model.BattleAction;
import com.lucas.restgame.model.BattleManager;
import com.lucas.restgame.model.BattleStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BattleManagerTests {
    
    private Battle battle;
    @Mock
    private BattleManager battleManager;

    @BeforeEach
    public void setup() {
        Player player = new Player("Player", 100, 20, 10);
        Enemy enemy = new Enemy("Enemy", 100, 20, 10);
        battle = new Battle(player, enemy);
        battleManager = new BattleManager(battle);
    }

    /*
     Stub the first Enemy to always perform a given BattleAction.
     TODO: support for specifying which enemy/enemies to stub
     */
    public void forceEnemyBattleAction(BattleAction action) {
        Enemy stubEnemy = spy(battle.getEnemies().get(0));
        when(stubEnemy.battleAction()).thenReturn(action);
        List<Enemy> enemies = new ArrayList<>(List.of(stubEnemy));
        battle.setEnemies(enemies);
    }

    public void forceEnemyDodgeOutcome(boolean outcome) {
        Enemy enemy = spy(battle.getEnemies().get(0));
        when(enemy.dodge()).thenReturn(outcome);
        List<Enemy> enemies = new ArrayList<>(List.of(enemy));
        battle.setEnemies(enemies);
    }

    public void forcePlayerDodgeOutcome(boolean outcome) {
        Player player = spy(battle.getPlayer());
        when(player.dodge()).thenReturn(outcome);
        battle.setPlayer(player);
    }


    @Test
    /*
    Given player uses ATTACK
    When enemy uses ATTACK
    Then both should lose health
     */
    public void playerAttacksEnemyAttacks() {
        // stub battleAction() to ATTACK
        forceEnemyBattleAction(BattleAction.ATTACK);

        // starting health
        Enemy enemy = battle.getEnemies().get(0);
        Player player = battle.getPlayer();
        int enemyHealthBefore = enemy.getHealth();
        int playerHealthBefore = player.getHealth();
        battleManager.performTurn(BattleAction.ATTACK);

        assertTrue(enemyHealthBefore > enemy.getHealth());
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
        forceEnemyBattleAction(BattleAction.ATTACK);

        Enemy enemy = battle.getEnemies().get(0);
        Player player = battle.getPlayer();
        // full damage amount subtracted from player health
        int undefendedHealth = player.getHealth()
                - (enemy.getPower() - player.getDefense());
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
        forceEnemyBattleAction(BattleAction.DEFEND);

        Enemy enemy = battle.getEnemies().get(0);
        Player player = battle.getPlayer();
        // full damage amount subtracted from enemy health
        int undefendedHealth = enemy.getHealth()
                - (player.getPower() - enemy.getDefense());

        // get actual health after defending
        battleManager.performTurn(BattleAction.ATTACK);

        assertTrue(enemy.getHealth() > undefendedHealth);
    }

    @Test
    /*
    Given player uses DODGE
    When enemy uses DEFEND
    Then enemy should have priority
     */
    public void priorityWhenPlayerDodgesEnemyDefends() {
        // stub battleAction() to DEFEND
        forceEnemyBattleAction(BattleAction.DEFEND);

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
        forceEnemyBattleAction(BattleAction.DODGE);

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
        forceEnemyBattleAction(BattleAction.DODGE);

        Enemy enemy = battle.getEnemies().get(0);
        Player player = battle.getPlayer();
        int startPlayerHealth = player.getHealth();
        int startEnemyHealth = enemy.getHealth();
        battleManager.performTurn(BattleAction.DODGE);

        assertEquals(startPlayerHealth, player.getHealth());
        assertEquals(startEnemyHealth, enemy.getHealth());
//        assertEquals(-1, battle.getPriority());
    }

    @Test
    /*
    Given there is one enemy with low health
    When player uses ATTACK
    Then enemy should be deleted
    And status should be VICTORY
     */
    public void playerVictory() {
        // stub battleAction() to ATTACK
        forceEnemyBattleAction(BattleAction.ATTACK);

        Enemy enemy = battle.getEnemies().get(0);
        enemy.setHealth(1);
        battleManager.performTurn(BattleAction.ATTACK);

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
        // stub battleAction() to ATTACK
        forceEnemyBattleAction(BattleAction.ATTACK);

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
        forceEnemyBattleAction(BattleAction.ATTACK);

        Enemy enemy = battle.getEnemies().get(0);
        Player player = battle.getPlayer();
        int enemyInitialHealth = enemy.getHealth();
        battle.setPriority(1);
        player.setHealth(1);
        battleManager.performTurn(BattleAction.ATTACK);

        assertEquals(enemyInitialHealth, enemy.getHealth());
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
        forceEnemyBattleAction(BattleAction.ATTACK);

        Enemy enemy = battle.getEnemies().get(0);
        Player player = battle.getPlayer();
        int playerInitialHealth = player.getHealth();
        battle.setPriority(0);
        enemy.setHealth(1);
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
    public void playerAttackEnemyDodgeSuccessful() {
        // stub battleAction() to DODGE
        // stub dodge() to always succeed
        Enemy enemy = spy(battle.getEnemies().get(0));
        when(enemy.battleAction()).thenReturn(BattleAction.DODGE);
        when(enemy.dodge()).thenReturn(true);
        List<Enemy> enemies = new ArrayList<>(List.of(enemy));
        battle.setEnemies(enemies);

        int startingEnemyHealth = battle.getEnemies().get(0).getHealth();
        battleManager.performTurn(BattleAction.ATTACK);

        assertEquals(startingEnemyHealth, battle.getEnemies().get(0).getHealth());
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
        // stub dodge() to always fail
        Enemy enemy = spy(battle.getEnemies().get(0));
        when(enemy.battleAction()).thenReturn(BattleAction.DODGE);
        when(enemy.dodge()).thenReturn(false);
        List<Enemy> enemies = new ArrayList<>(List.of(enemy));
        battle.setEnemies(enemies);

        int startingEnemyHealth = battle.getEnemies().get(0).getHealth();
        battleManager.performTurn(BattleAction.ATTACK);

//        verify(stubbedEnemy).dodge();
        assertTrue(battle.getEnemies().get(0).getHealth() < startingEnemyHealth);
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
        // stub battleAction() to ATTACK
        Enemy enemy = spy(battle.getEnemies().get(0));
        when(enemy.battleAction()).thenReturn(BattleAction.ATTACK);
        List<Enemy> enemies = new ArrayList<>(List.of(enemy));
        battle.setEnemies(enemies);
        // stub dodge() to always succeed
        Player player = spy(battle.getPlayer());
        when(player.dodge()).thenReturn(true);
        battle.setPlayer(player);

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
        // stub battleAction() to ATTACK
        Enemy enemy = spy(battle.getEnemies().get(0));
        when(enemy.battleAction()).thenReturn(BattleAction.ATTACK);
        List<Enemy> enemies = new ArrayList<>(List.of(enemy));
        battle.setEnemies(enemies);
        // stub dodge() to always fail
        Player player = spy(battle.getPlayer());
        when(player.dodge()).thenReturn(false);
        battle.setPlayer(player);

        int startingPlayerHealth = battle.getPlayer().getHealth();
        battleManager.performTurn(BattleAction.DODGE);

        assertTrue(battle.getPlayer().getHealth() < startingPlayerHealth);
        assertEquals(-1, battle.getPriority());
    }
}
