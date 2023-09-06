package com.lucas.restgame.model;

import com.lucas.restgame.entity.Battle;
import com.lucas.restgame.entity.Enemy;
import com.lucas.restgame.entity.Entity;
import com.lucas.restgame.entity.Player;

import java.util.List;

public class BattleManager {

    /*
    Idea:
    Set-keyed map with function values
    Call function to apply effects and add text
     */

    private boolean playerMovesFirst(Battle battle) {
        boolean playerMovesFirst;
        if (battle.getPriority() == -1) {
            // random damage order
            playerMovesFirst = Math.random() < 0.5f;
        } else {
            playerMovesFirst = battle.getPriority() == 0;
        }
        return playerMovesFirst;
    }

    private void reportActions(
            Battle battle,
            Entity entity1, BattleAction action1,
            Entity entity2, BattleAction action2) {
        battle.addText(
                String.format(
                        "%s uses %s!",
                        entity1.getName(),
                        action1.name()));
        battle.addText(
                String.format(
                        "%s uses %s!",
                        entity2.getName(),
                        action2.name()));
    }

    /*
    calculates and applies damage based on power and defense, then
    updates battle text. does nothing if either entity is dead.
     */
    private void applyAttackDamage(
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
    private void applyAttackDamage(
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

    private void awardPriority(Battle battle, Entity recipient) {
        // give priority to recipient
        // TODO: better priority system for parties
        int priority = (recipient.getClass() == Player.class) ? 0 : 1;
        battle.setPriority(priority);
        // update battle text
        battle.addText(
                String.format(
                        "%s is now poised to act.",
                        recipient.getName()));
    }

    private void handleAttackAttack(
            Battle battle, Entity attacker1, Entity attacker2) {
        // apply damage to second entity
        applyAttackDamage(battle, attacker1, attacker2);
        // apply damage to first entity
        applyAttackDamage(battle, attacker2, attacker1);
    }

    private void handleAttackDodge(
            Battle battle, Entity attacker, Entity dodger) {
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

    private void handleAttackDefend(
            Battle battle, Entity attacker, Entity defender) {
        battle.addText(
                String.format(
                        "%s readies their shield.",
                        defender.getName()));
        // apply halved damage to defender
        applyAttackDamage(battle, attacker, defender, 0.5f);
    }

    private void handleAttackSpell(
            Battle battle, Entity attacker, Entity caster) {
        // apply damage to caster
        applyAttackDamage(battle, attacker, caster);
        // apply spell effect to attacker if no active effects
        // applySpell(battle, caster, attacker); // TODO: implement spells!
    }

    private void handleDefendDefend(
            Battle battle, Entity defender1, Entity defender2) {
        // update battle text
        battle.addText("Nothing happens...");
    }

    private void handleDefendDodge(
            Battle battle, Entity defender, Entity dodger) {
        // update battle text
        battle.addText(
                String.format(
                        "%s is unfazed by %s's movement.",
                        defender.getName(),
                        dodger.getName()));
        // give priority to defender
        awardPriority(battle, defender);
    }

    private void handleDefendSpell(
            Battle battle, Entity defender, Entity caster) {
        // update battle text
        battle.addText(
                String.format(
                        "%s prepares for a magic attack.",
                        defender.getName()));
        // apply spell on defender with halved duration
        // applySpell(battle, caster, defender, 0.5f); // TODO: implement spells!
    }

    private void handleDodgeDodge(
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

    private void handleDodgeSpell(
            Battle battle, Entity dodger, Entity caster) {
        // update battle text
        battle.addText(
                String.format(
                        "%s watches %s's movements closely.",
                        caster.getName(),
                        dodger.getName()));
        // apply spell on dodger
        // applySpell(battle, caster, dodger); // TODO: implement spells!
    }

    private void handleSpellSpell(
            Battle battle, Entity caster1, Entity caster2) {
        // apply spell on caster 2 if no active effects
        // applySpell(battle, caster1, caster2); // TODO: implement spells!
        // apply spell on caster 1 if no active effects
        // applySpell(battle, caster2, caster1); // TODO: implement spells!
    }

    public Battle simulateBattle(
            Battle battle, BattleAction playerAction) {
        if (battle.getStatus() != BattleStatus.ONGOING) {
            return battle;
        }

        boolean playerDodged = false;
        boolean enemyDodged = false;
        int enemyHit = 0;
        int playerHit = 0;

        Player player = battle.getPlayer();
        List<Enemy> enemies = battle.getEnemies();
        // TODO: support for targeting specific enemy
        Enemy enemy = enemies.get(0);
        BattleAction enemyAction = enemy.battleAction();

        // placeholder dodge probabilities
        if (enemyAction == BattleAction.DODGE
                && playerAction == BattleAction.ATTACK) {
            enemyDodged = coinFlip(0.5f);
        }
        if (playerAction == BattleAction.DODGE
                && enemyAction == BattleAction.ATTACK) {
            playerDodged = coinFlip(0.5f);
        }

        // calculate base attack damage
        if (enemyAction == BattleAction.ATTACK) {
            enemyHit = enemy.getPower() - player.getDefense();
        }
        if (playerAction == BattleAction.ATTACK) {
            playerHit = player.getPower() - enemy.getDefense();
        }

        // factor in defends and dodges and priority
        if (enemyAction == BattleAction.DEFEND) {
            if (playerAction == BattleAction.ATTACK) {
                playerHit = Math.round(playerHit * 0.5f);
            } else if (playerAction == BattleAction.DODGE) {
                // Defend -> Dodge guarantees defender priority
                battle.setPriority(1);
            }
        } else if (enemyDodged) {
            playerHit = 0;
            battle.setPriority(1);
        }
        if (playerAction == BattleAction.DEFEND) {
            if (enemyAction == BattleAction.ATTACK) {
                enemyHit = Math.round(enemyHit * 0.5f);
            } else if (enemyAction == BattleAction.DODGE) {
                // Defend -> Dodge guarantees defender priority
                battle.setPriority(0);
            }
        } else if (playerDodged) {
            enemyHit = 0;
            battle.setPriority(0);
        }

        // determine turn order
        boolean playerMovesFirst;
        if (battle.getPriority() == -1) {
            // random damage order
            playerMovesFirst = Math.random() < 0.5f;
        } else {
            playerMovesFirst = battle.getPriority() == 0;
        }

        // apply damage, set status, update splash text
        // TODO apply spell effects
        // player attacks first
        if (playerMovesFirst) {
            battle.addText(
                    String.format(
                            "%s uses %s!",
                            player.getName(),
                            playerAction.toString()
                    )
            );
            // player deals damage
            if (playerHit > 0) {
                enemy.setHealth(Math.max(0, enemy.getHealth() - playerHit));
                battle.addText(
                        String.format(
                                "%s hits %s for %s damage.",
                                player.getName(),
                                enemy.getName(),
                                playerHit
                        )
                );
            } else {
                // enemy dodges successfully
                if (enemyDodged) {
                    battle.addText(
                            String.format(
                                    "%s dodges %s's attack!",
                                    enemy.getName(),
                                    player.getName()
                            )
                    );
                }  else if (enemyAction == BattleAction.DODGE) {
                    // enemy fails to dodge
                    battle.addText(
                            String.format(
                                    "%s fails to dodge %s's attack.",
                                    enemy.getName(),
                                    player.getName()
                            )
                    );
                }
            }
            // enemy dies
            if (enemy.getHealth() == 0) {
                // TODO add status to prevent targeting or attacking
                battle.addText(
                        String.format(
                                "%s has killed %s.",
                                player.getName(),
                                enemy.getName()
                        )
                );
                enemies.remove(enemy);
                if (enemies.isEmpty()) {
                    battle.setStatus(BattleStatus.VICTORY);
                }
            } else { // enemy survives
                battle.addText(
                        String.format(
                                "%s uses %s!",
                                enemy.getName(),
                                enemyAction.toString()
                        )
                );
                // enemy deals damage
                if (enemyHit > 0) {
                    player.setHealth(Math.max(0, player.getHealth() - enemyHit));
                    battle.addText(
                            String.format(
                                    "%s hits %s for %s damage.",
                                    enemy.getName(),
                                    player.getName(),
                                    enemyHit
                            )
                    );
                    // player dies
                    if (player.getHealth() == 0) {
                        battle.addText(
                                String.format(
                                        "%s has killed %s.",
                                        player.getName(),
                                        enemy.getName()
                                )
                        );
                        battle.setStatus(BattleStatus.DEFEAT);
                    }
                } else { // enemy deals no damage
                    // player dodges successfully
                    if (playerDodged) {
                        battle.addText(
                                String.format(
                                        "%s dodges %s's attack!",
                                        player.getName(),
                                        enemy.getName()
                                )
                        );
                    } else if (playerAction == BattleAction.DODGE) {
                        // player fails to dodge
                        battle.addText(
                                String.format(
                                        "%s fails to dodge %s's attack.",
                                        player.getName(),
                                        enemy.getName()
                                )
                        );
                    }
                }
            }
        } else { // enemy attacks first
            battle.addText(
                    String.format(
                            "%s uses %s!",
                            enemy.getName(),
                            enemyAction.toString()
                    )
            );
            // enemy deals damage
            if (enemyHit > 0) {
                player.setHealth(Math.max(0, player.getHealth() - enemyHit));
                battle.addText(
                        String.format(
                                "%s hits %s for %s damage.",
                                enemy.getName(),
                                player.getName(),
                                enemyHit
                        )
                );
            } else { // enemy deals no damage
                // player dodges
                if (playerDodged) {
                    battle.addText(
                            String.format(
                                    "%s dodges %s's attack!",
                                    player.getName(),
                                    enemy.getName()
                            )
                    );
                } else if (playerAction == BattleAction.DODGE) {
                    // player fails to dodge
                    battle.addText(
                            String.format(
                                    "%s fails to dodge %s's attack.",
                                    player.getName(),
                                    enemy.getName()
                            )
                    );
                }
            }
            // player dies
            if (player.getHealth() == 0) {
                battle.addText(
                        String.format(
                                "%s has killed %s.",
                                player.getName(),
                                enemy.getName()
                        )
                );
                battle.setStatus(BattleStatus.DEFEAT);
            } else { // player survives
                battle.addText(
                        String.format(
                                "%s uses %s!",
                                player.getName(),
                                playerAction.toString()
                        )
                );
                // player deals damage
                if (playerHit > 0) {
                    enemy.setHealth(Math.max(0, enemy.getHealth() - playerHit));
                    battle.addText(
                            String.format(
                                    "%s hits %s for %s damage.",
                                    player.getName(),
                                    enemy.getName(),
                                    playerHit
                            )
                    );
                    // enemy dies
                    if (enemy.getHealth() == 0) {
                        // TODO add status to prevent targeting or attacking
                        battle.addText(
                                String.format(
                                        "%s has killed %s.",
                                        player.getName(),
                                        enemy.getName()
                                )
                        );
                        enemies.remove(enemy);
                        if (enemies.isEmpty()) {
                            battle.setStatus(BattleStatus.VICTORY);
                        }
                    }
                }
            }
        }
        return battle;
    }

    public boolean coinFlip(float odds) {
        return Math.random() < odds;
    }
}
