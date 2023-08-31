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

        assertSame(battle.getPriority(), 1);
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

        assertSame(battle.getPriority(), 0);
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

        assertSame(startPlayerHealth, endPlayerHealth);
        assertSame(startEnemyHealth, endEnemyHealth);
        assertSame(battle.getPriority(), -1);
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
        assertSame(battle.getStatus(), BattleStatus.VICTORY);
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

        assertSame(battle.getPlayer().getHealth(), 0);
        assertSame(battle.getStatus(), BattleStatus.DEFEAT);
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

        assertSame(enemy.getHealth(), enemyInitialHealth);
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

        assertSame(battle.getPlayer().getHealth(), playerInitialHealth);
    }
}
