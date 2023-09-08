package com.lucas.restgame.model;

import com.lucas.restgame.entity.Battle;
import com.lucas.restgame.entity.Enemy;
import com.lucas.restgame.entity.Entity;
import com.lucas.restgame.entity.Player;

import java.util.*;

public class BattleManager {
    
    private final Battle battle;

    // TODO change to factory/DI construction
    public BattleManager(Battle battle) {
        if (battle.getStatus() != BattleStatus.ONGOING) {
            throw new IllegalArgumentException("Battle has already concluded");
        }
        this.battle = battle;
    }

    private boolean playerMovesFirst() {
        if (getBattlePriority() == -1) {
            // random damage order
            return Math.random() < 0.5f;
        } else {
            return getBattlePriority() == 0;
        }
    }

    private void addBattleText(String text) {
        this.battle.addText(text);
    }

    private void clearBattleText() {
        this.battle.resetText();
    }

    private BattleStatus getBattleStatus() {
        return this.battle.getStatus();
    }
    private void setBattleStatus(BattleStatus status) {
        this.battle.setStatus(status);
    }

    private int getBattlePriority() {
        return this.battle.getPriority();
    }

    private void setBattlePriority(int priority) {
        this.battle.setPriority(priority);
    }

    private Player getPlayer() {
        return this.battle.getPlayer();
    }

    private List<Enemy> getEnemies() {
        return this.battle.getEnemies();
    }

    private void reportAction(Entity entity1, BattleAction action1) {
        addBattleText(
                String.format(
                        "%s uses %s!",
                        entity1.getName(),
                        action1.name()));
    }

    /*
    calculates and applies damage based on power and defense, then
    updates battle text. does nothing if either entity is dead.
     */
    private void applyAttackDamage(Entity attacker, Entity target) {
        // avoid redundant operations
        if (target.isDead() || attacker.isDead()) {
            return;
        }
        // calculate damage
        int damage = attacker.getPower() - target.getDefense();
        // apply damage
        target.setHealth(Math.max(0, target.getHealth() - damage));
        // update battle text
        addBattleText(
                String.format(
                        "%s hits %s for %s damage!",
                        attacker.getName(),
                        target.getName(),
                        damage));
        // report target death if needed
        if (target.isDead()) {
            addBattleText(
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
    private void applyAttackDamage(
            Entity attacker, Entity target, float modifier) {
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
        addBattleText(
                String.format(
                        "%s hits %s for %s damage!",
                        attacker.getName(),
                        target.getName(),
                        damage));
        // report target death if needed
        if (target.isDead()) {
            addBattleText(
                    String.format(
                            "%s has killed %s.",
                            attacker.getName(),
                            target.getName()));
        }
    }

    private void awardPriorityTo(Entity recipient) {
        // give priority to recipient
        // TODO: better priority system for parties
        int priority = (recipient.getClass() == Player.class) ? 0 : 1;
        setBattlePriority(priority);
        // update battle text
        addBattleText(
                String.format(
                        "%s is poised to act.",
                        recipient.getName()));
    }

    // firstAttacker will deal damage first
    private void handleAttackAttack(Entity firstAttacker, Entity secondAttacker) {
        // first attacker deals damage
        applyAttackDamage(firstAttacker, secondAttacker);
        // second attacker deals damage
        applyAttackDamage(secondAttacker, firstAttacker);
    }

    private void handleAttackDodge(Entity attacker, Entity dodger) {
        // roll for dodge
//        boolean dodgeSuccess = coinFlip(0.5f); // replace with entity dodge chance
        boolean dodgeSuccess = dodger.attemptDodge();
        if (dodgeSuccess) {
            // update battle text
            addBattleText(
                    String.format(
                            "%s dodges %s's attack!",
                            dodger.getName(),
                            attacker.getName()));
            // award priority
            awardPriorityTo(dodger);
        } else {
            // update battle text
            addBattleText(
                    String.format(
                            "%s fails to dodge %s's attack.",
                            dodger.getName(),
                            attacker.getName()));
            // apply damage to dodger
            applyAttackDamage(attacker, dodger);
        }
    }

    private void handleAttackDefend(Entity attacker, Entity defender) {
        addBattleText(
                String.format(
                        "%s readies their shield.",
                        defender.getName()));
        // apply halved damage to defender
        applyAttackDamage(attacker, defender, 0.5f);
    }

    private void handleAttackSpell(Entity attacker, Entity caster) {
        // apply damage to caster
        applyAttackDamage(attacker, caster);
        // apply spell effect to attacker if no active effects
        // applySpell(battle, caster, attacker); // TODO: implement spells!
    }

    private void handleDefendDefend(Entity defender1, Entity defender2) {
        // update battle text
        addBattleText("Nothing happens...");
    }

    private void handleDefendDodge(Entity defender, Entity dodger) {
        // update battle text
        addBattleText(
                String.format(
                        "%s is unfazed by %s's movement.",
                        defender.getName(),
                        dodger.getName()));
        // give priority to defender
        awardPriorityTo(defender);
    }

    private void handleDefendSpell(Entity defender, Entity caster) {
        // update battle text
        addBattleText(
                String.format(
                        "%s prepares for a magic attack.",
                        defender.getName()));
        // apply spell on defender with halved duration
        // applySpell(battle, caster, defender, 0.5f); // TODO: implement spells!
    }

    private void handleDodgeDodge(Entity dodger1, Entity dodger2) {
        // update battle text
        addBattleText("Both combatants attempt to reposition.");
        // roll for dodge for both
        boolean dodge1Success = coinFlip(0.5f);
        boolean dodge2Success = coinFlip(0.5f);
        // nothing happens if both succeed or both fail
        if (dodge1Success == dodge2Success) {
            addBattleText("Neither manages to gain the advantage.");
        } else {
            // award priority to successful dodger
            Entity successfulDodger = dodge1Success ? dodger1 : dodger2;
            addBattleText(
                    String.format(
                            "%s finds better footing!",
                            successfulDodger.getName()));
            awardPriorityTo(successfulDodger);
        }
    }

    private void handleDodgeSpell(Entity dodger, Entity caster) {
        // update battle text
        addBattleText(
                String.format(
                        "%s watches %s's movements closely.",
                        caster.getName(),
                        dodger.getName()));
        // apply spell on dodger
        // applySpell(battle, caster, dodger); // TODO: implement spells!
    }

    // applies spells in order of parameters
    private void handleSpellSpell(Entity caster1, Entity caster2) {
        // apply spell on caster 2 if no active effects
        // applySpell(battle, caster1, caster2); // TODO: implement spells!
        // apply spell on caster 1 if no active effects
        // applySpell(battle, caster2, caster1); // TODO: implement spells!
    }

    public boolean coinFlip(float odds) {
        return Math.random() < odds;
    }

    public Battle performTurn(BattleAction playerAction) {
        // do nothing if battle has already ended
        if (getBattleStatus() != BattleStatus.ONGOING) {
            return this.battle;
        }

        // reset battle text for turn
//        clearBattleText(); // off for debugging

        Player player = getPlayer();
        List<Enemy> enemies = getEnemies();
        // TODO: support for targeting specific enemy
        Enemy enemy = enemies.get(0);
        BattleAction enemyAction = enemy.battleAction();

        // determine turn order
        boolean playerMovesFirst = playerMovesFirst();

        // update battle text with actions used
        if (playerMovesFirst) {
            reportAction(player, playerAction);
            reportAction(enemy, enemyAction);
        } else {
            reportAction(enemy, enemyAction);
            reportAction(player, playerAction);
        }

        // reset priority to neutral
        setBattlePriority(-1);

        // call action handler function
        Set<BattleAction> turnType = Set.copyOf(List.of(playerAction, enemyAction));
        BMFunc turnHandler = actionHandlers.get(turnType);
        turnHandler.run(player, enemy, playerMovesFirst, playerAction);

        // TODO replace with method
        // handle deaths and status
        if (player.isDead()) {
            // report game over
            addBattleText(
                    String.format(
                            "%s has killed %s.",
                            enemy.getName(),
                            player.getName()));
            addBattleText("The battle is lost.");
            // update status
            setBattleStatus(BattleStatus.DEFEAT);
        } else if (enemy.isDead()) {
            // report death
            addBattleText(
                    String.format(
                            "%s has killed %s.",
                            player.getName(),
                            enemy.getName()));
            // delete enemy
            enemies.remove(enemy);
            // check for victory
            if (enemies.isEmpty()) {
                setBattleStatus(BattleStatus.VICTORY);
                addBattleText(
                        String.format(
                                "%s is victorious!",
                                player.getName()));
            }
        }

        return battle;
    }

    private final Map<Set<BattleAction>, BMFunc> actionHandlers = Map.of(
            // ATTACK ATTACK
            Set.copyOf(List.of(BattleAction.ATTACK)),
            (friendly, enemy, friendlyFirst, action) -> {
                if (friendlyFirst) {
                    handleAttackAttack((Entity) friendly, (Entity) enemy);
                } else {
                    handleAttackAttack((Entity) enemy, (Entity) friendly);
                }
            },

            // ATTACK DEFEND
            Set.copyOf(List.of(BattleAction.ATTACK, BattleAction.DEFEND)),
            (friendly, enemy, friendlyFirst, action) -> {
                if (action == BattleAction.ATTACK) {
                    handleAttackDefend((Entity) friendly, (Entity) enemy);
                } else {
                    handleAttackDefend((Entity) enemy, (Entity) friendly);
                }
            },

            // ATTACK DODGE
            Set.copyOf(List.of(BattleAction.ATTACK, BattleAction.DODGE)),
            (friendly, enemy, friendlyFirst, action) -> {
                if (action == BattleAction.ATTACK) {
                    handleAttackDodge((Entity) friendly, (Entity) enemy);
                } else {
                    handleAttackDodge((Entity) enemy, (Entity) friendly);
                }
            },

            // ATTACK SPELL
            Set.copyOf(List.of(BattleAction.ATTACK, BattleAction.SPELL)),
            (friendly, enemy, friendlyFirst, action) -> {
                if (action == BattleAction.ATTACK) {
                    handleAttackSpell((Entity) friendly, (Entity) enemy);
                } else {
                    handleAttackSpell((Entity) enemy, (Entity) friendly);
                }
            },

            // DEFEND DEFEND
            Set.copyOf(List.of(BattleAction.DEFEND)),
            (friendly, enemy, friendlyFirst, action) ->
                handleDefendDefend((Entity) friendly, (Entity) enemy),

            // DEFEND DODGE
            Set.copyOf(List.of(BattleAction.DEFEND, BattleAction.DODGE)),
            (friendly, enemy, friendlyFirst, action) -> {
                if (action == BattleAction.DEFEND) {
                    handleDefendDodge((Entity) friendly, (Entity) enemy);
                } else {
                    handleDefendDodge((Entity) enemy, (Entity) friendly);
                }
            },

            // DEFEND SPELL
            Set.copyOf(List.of(BattleAction.DEFEND, BattleAction.SPELL)),
            (friendly, enemy, friendlyFirst, action) -> {
                if (action == BattleAction.DEFEND) {
                    handleDefendSpell((Entity) friendly, (Entity) enemy);
                } else {
                    handleDefendSpell((Entity) enemy, (Entity) friendly);
                }
            },

            // DODGE DODGE
            Set.copyOf(List.of(BattleAction.DODGE)),
            (friendly, enemy, friendlyFirst, a) ->
                handleDodgeDodge((Entity) friendly, (Entity) enemy),

            // DODGE SPELL
            Set.copyOf(List.of(BattleAction.DODGE, BattleAction.SPELL)),
            (friendly, enemy, friendlyFirst, action) -> {
                if (action == BattleAction.DODGE) {
                    handleDodgeSpell((Entity) friendly, (Entity) enemy);
                } else {
                    handleDodgeSpell((Entity) enemy, (Entity) friendly);
                }
            },

            // SPELL SPELL
            Set.copyOf(List.of(BattleAction.SPELL)),
            (friendly, enemy, friendlyFirst, action) -> {
                if (friendlyFirst) {
                    handleSpellSpell((Entity) friendly, (Entity) enemy);
                } else {
                    handleSpellSpell((Entity) enemy, (Entity) friendly);
                }
            }
    );

    @FunctionalInterface
    private interface BMFunc<Entity, BattleAction> {
        public void run(
                Entity entity1,
                Entity entity2,
                boolean entity1First,
                BattleAction action);
    }
}
