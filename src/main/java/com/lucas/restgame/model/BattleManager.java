package com.lucas.restgame.model;

import com.lucas.restgame.entity.Battle;
import com.lucas.restgame.entity.Enemy;
import com.lucas.restgame.entity.Entity;
import com.lucas.restgame.entity.Player;

import java.util.*;

public class BattleManager {

    private static boolean playerMovesFirst(Battle battle) {
        boolean playerMovesFirst;
        if (battle.getPriority() == -1) {
            // random damage order
            playerMovesFirst = Math.random() < 0.5f;
        } else {
            playerMovesFirst = battle.getPriority() == 0;
        }
        return playerMovesFirst;
    }

    private static void reportAction(
            Battle battle,
            Entity entity1, BattleAction action1) {
        battle.addText(
                String.format(
                        "%s uses %s!",
                        entity1.getName(),
                        action1.name()));
    }

    /*
    calculates and applies damage based on power and defense, then
    updates battle text. does nothing if either entity is dead.
     */
    private static void applyAttackDamage(
            Battle battle, Entity attacker, Entity target) {
        // avoid redundant operations
        if (target.isDead() || attacker.isDead()) {
            return;
        }
        // calculate damage
        int damage = attacker.getPower() - target.getDefense();
        // apply damage
        target.setHealth(Math.max(0, target.getHealth() - damage));
        // update battle text
        battle.addText(
                String.format(
                        "%s hits %s for %s damage!",
                        attacker.getName(),
                        target.getName(),
                        damage));
        // report target death if needed
        if (target.isDead()) {
            battle.addText(
                    String.format(
                            "%s has killed %s.",
                            attacker.getName(),
                            target.getName()));
        }
    }

    /*
    calculates and applies damage based on power, defense, and a damage
    multiplier, then updates battle text. does nothing if either entity is dead
     */
    private static void applyAttackDamage(
            Battle battle, Entity attacker, Entity target, float modifier) {
        // avoid redundant operations
        if (target.isDead() || attacker.isDead()) {
            return;
        }
        // calculate damage
        int damage = Math.round(
                (attacker.getPower() - target.getDefense()) * modifier);
        // apply damage
        target.setHealth(Math.max(0, target.getHealth() - damage));
        // update battle text
        battle.addText(
                String.format(
                        "%s hits %s for %s damage!",
                        attacker.getName(),
                        target.getName(),
                        damage));
        // report target death if needed
        if (target.isDead()) {
            battle.addText(
                    String.format(
                            "%s has killed %s.",
                            attacker.getName(),
                            target.getName()));
        }
    }

    private static void awardPriority(Battle battle, Entity recipient) {
        // give priority to recipient
        // TODO: better priority system for parties
        int priority = (recipient.getClass() == Player.class) ? 0 : 1;
        battle.setPriority(priority);
        // update battle text
        battle.addText(
                String.format(
                        "%s is poised to act.",
                        recipient.getName()));
    }

    // first entity moves first
    private static void handleAttackAttack(
            Battle battle, Entity attacker1, Entity attacker2) {
        // apply damage to second entity
        applyAttackDamage(battle, attacker1, attacker2);
        // apply damage to first entity
        applyAttackDamage(battle, attacker2, attacker1);
    }

    private static void handleAttackDodge(
            Battle battle,
            Entity player,
            Entity enemy,
            BattleAction playerAction) {
        // determine roles
        Entity attacker, dodger;
        if (playerAction == BattleAction.ATTACK) {
            attacker = player;
            dodger = player;
        } else {
            attacker = enemy;
            dodger = player;
        }
        // roll for dodge
        boolean dodgeSuccess = coinFlip(0.5f); // replace with entity dodge chance
        if (dodgeSuccess) {
            // update battle text
            battle.addText(
                    String.format(
                            "%s dodges %s's attack!",
                            dodger.getName(),
                            attacker.getName()));
            // award priority
            awardPriority(battle, dodger);
        } else {
            // update battle text
            battle.addText(
                    String.format(
                            "%s fails to dodge %s's attack.",
                            dodger.getName(),
                            attacker.getName()));
            // apply damage to dodger
            applyAttackDamage(battle, attacker, dodger);
        }
    }

    private static void handleAttackDefend(
            Battle battle,
            Entity player,
            Entity enemy,
            BattleAction playerAction) {
        // determine roles
        Entity attacker, defender;
        if (playerAction == BattleAction.ATTACK) {
            attacker = player;
            defender = enemy;
        } else {
            attacker = enemy;
            defender = player;
        }
        battle.addText(
                String.format(
                        "%s readies their shield.",
                        defender.getName()));
        // apply halved damage to defender
        applyAttackDamage(battle, attacker, defender, 0.5f);
    }

    private static void handleAttackSpell(
            Battle battle,
            Entity player,
            Entity enemy,
            BattleAction playerAction) {
        // determine roles
        Entity attacker, caster;
        if (playerAction == BattleAction.ATTACK) {
            attacker = player;
            caster = enemy;
        } else {
            attacker = enemy;
            caster = player;
        }
        // apply damage to caster
        applyAttackDamage(battle, attacker, caster);
        // apply spell effect to attacker if no active effects
        // applySpell(battle, caster, attacker); // TODO: implement spells!
    }

    private static void handleDefendDefend(
            Battle battle, Entity defender1, Entity defender2) {
        // update battle text
        battle.addText("Nothing happens...");
    }

    private static void handleDefendDodge(
            Battle battle,
            Entity player,
            Entity enemy,
            BattleAction playerAction) {
        // determine roles
        Entity defender, dodger;
        if (playerAction == BattleAction.DEFEND) {
            defender = player;
            dodger = enemy;
        } else {
            defender = enemy;
            dodger = player;
        }
        // update battle text
        battle.addText(
                String.format(
                        "%s is unfazed by %s's movement.",
                        defender.getName(),
                        dodger.getName()));
        // give priority to defender
        awardPriority(battle, defender);
    }

    private static void handleDefendSpell(
            Battle battle,
            Entity player,
            Entity enemy,
            BattleAction playerAction) {
        // determine roles
        Entity defender, caster;
        if (playerAction == BattleAction.DEFEND) {
            defender = player;
            caster = enemy;
        } else {
            defender = enemy;
            caster = player;
        }
        // update battle text
        battle.addText(
                String.format(
                        "%s prepares for a magic attack.",
                        defender.getName()));
        // apply spell on defender with halved duration
        // applySpell(battle, caster, defender, 0.5f); // TODO: implement spells!
    }

    private static void handleDodgeDodge(
            Battle battle, Entity dodger1, Entity dodger2) {
        // update battle text
        battle.addText("Both combatants attempt to reposition.");
        // roll for dodge for both
        boolean dodge1Success = coinFlip(0.5f);
        boolean dodge2Success = coinFlip(0.5f);
        // nothing happens if both succeed or both fail
        if (dodge1Success == dodge2Success) {
            battle.addText("Neither manages to gain the advantage.");
        } else {
            // award priority to successful dodger
            Entity successfulDodger = dodge1Success ? dodger1 : dodger2;
            battle.addText(
                    String.format(
                            "%s finds better footing!",
                            successfulDodger.getName()));
            awardPriority(battle, successfulDodger);
        }
    }

    private static void handleDodgeSpell(
            Battle battle,
            Entity player,
            Entity enemy,
            BattleAction playerAction) {
        // determine roles
        Entity dodger, caster;
        if (playerAction == BattleAction.DODGE) {
            dodger = player;
            caster = enemy;
        } else {
            dodger = enemy;
            caster = player;
        }
        // update battle text
        battle.addText(
                String.format(
                        "%s watches %s's movements closely.",
                        caster.getName(),
                        dodger.getName()));
        // apply spell on dodger
        // applySpell(battle, caster, dodger); // TODO: implement spells!
    }

    // applies spells in order of parameters
    private static void handleSpellSpell(
            Battle battle, Entity caster1, Entity caster2) {
        // apply spell on caster 2 if no active effects
        // applySpell(battle, caster1, caster2); // TODO: implement spells!
        // apply spell on caster 1 if no active effects
        // applySpell(battle, caster2, caster1); // TODO: implement spells!
    }

    public static boolean coinFlip(float odds) {
        return Math.random() < odds;
    }

    public static Battle simulateBattle(
            Battle battle, BattleAction playerAction) {
        // do nothing if battle has already ended
        if (battle.getStatus() != BattleStatus.ONGOING) {
            return battle;
        }

        // reset priority to neutral
        battle.setPriority(-1);

        // reset battle text for turn
//        battle.resetText(); // off for debugging

        Player player = battle.getPlayer();
        List<Enemy> enemies = battle.getEnemies();
        // TODO: support for targeting specific enemy
        Enemy enemy = enemies.get(0);
        BattleAction enemyAction = enemy.battleAction();

        // determine turn order
        boolean playerMovesFirst;
        if (battle.getPriority() == -1) {
            // random damage order
            playerMovesFirst = Math.random() < 0.5f;
        } else {
            playerMovesFirst = battle.getPriority() == 0;
        }

        // update battle text with actions used
        if (playerMovesFirst) {
            reportAction(battle, player, playerAction);
            reportAction(battle, enemy, enemyAction);
        } else {
            reportAction(battle, enemy, enemyAction);
            reportAction(battle, player, playerAction);
        }

        // call action handler function
        Set<BattleAction> turnType = Set.copyOf(List.of(playerAction, enemyAction));
        BMFunc turnHandler = actionHandlers.get(turnType);
        turnHandler.run(battle, player, enemy, playerAction);

        // handle deaths and status
        if (player.isDead()) {
            // report game over
            battle.addText(
                    String.format(
                            "%s has killed %s.",
                            enemy.getName(),
                            player.getName()));
            battle.addText("The battle is lost.");
            // update status
            battle.setStatus(BattleStatus.DEFEAT);
        } else if (enemy.isDead()) {
            // report death
            battle.addText(
                    String.format(
                            "%s has killed %s.",
                            player.getName(),
                            enemy.getName()));
            // delete enemy
            enemies.remove(enemy);
            // check for victory
            if (enemies.isEmpty()) {
                battle.setStatus(BattleStatus.VICTORY);
                battle.addText(
                        String.format(
                                "%s is victorious!",
                                player.getName()));
            }
        }

        return battle;
    }

    private static final Map<Set<BattleAction>, BMFunc> actionHandlers = Map.of(
            // ATTACK ATTACK
            Set.copyOf(List.of(BattleAction.ATTACK, BattleAction.ATTACK)),
            (b, e1, e2, a) ->
                    handleAttackAttack((Battle) b, (Entity) e1, (Entity) e2),
            // ATTACK DEFEND
            Set.copyOf(List.of(BattleAction.ATTACK, BattleAction.DEFEND)),
            (b, e1, e2, a) ->
                    handleAttackDefend((Battle) b, (Entity) e1, (Entity) e2, a),
            // ATTACK DODGE
            Set.copyOf(List.of(BattleAction.ATTACK, BattleAction.DODGE)),
            (b, e1, e2, a) ->
                    handleAttackDodge((Battle) b, (Entity) e1, (Entity) e2, a),
            // ATTACK SPELL
            Set.copyOf(List.of(BattleAction.ATTACK, BattleAction.SPELL)),
            (b, e1, e2, a) ->
                    handleAttackSpell((Battle) b, (Entity) e1, (Entity) e2, a),
            // DEFEND DEFEND
            Set.copyOf(List.of(BattleAction.DEFEND, BattleAction.DEFEND)),
            (b, e1, e2, a) ->
                    handleDefendDefend((Battle) b, (Entity) e1, (Entity) e2),
            // DEFEND DODGE
            Set.copyOf(List.of(BattleAction.DEFEND, BattleAction.DODGE)),
            (b, e1, e2, a) ->
                    handleDefendDodge((Battle) b, (Entity) e1, (Entity) e2, a),
            // DEFEND SPELL
            Set.copyOf(List.of(BattleAction.DEFEND, BattleAction.SPELL)),
            (b, e1, e2, a) ->
                    handleDefendSpell((Battle) b, (Entity) e1, (Entity) e2, a),
            // DODGE DODGE
            Set.copyOf(List.of(BattleAction.DODGE, BattleAction.DODGE)),
            (b, e1, e2, a) ->
                    handleDodgeDodge((Battle) b, (Entity) e1, (Entity) e2),
            // DODGE SPELL
            Set.copyOf(List.of(BattleAction.DODGE, BattleAction.SPELL)),
            (b, e1, e2, a) ->
                    handleDodgeSpell((Battle) b, (Entity) e1, (Entity) e2, a),
            // SPELL SPELL
            Set.copyOf(List.of(BattleAction.SPELL, BattleAction.SPELL)),
            (b, e1, e2, a) ->
                    handleSpellSpell((Battle) b, (Entity) e1, (Entity) e2)
    );

    @FunctionalInterface
    private interface BMFunc<Battle, Entity> {
        public void run(Battle battle, Entity entity1, Entity entity2, BattleAction playerAction);
    }
}
