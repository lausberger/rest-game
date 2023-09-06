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
        // apply priority if successful
        if (dodgeSuccess) {
            int priority = (dodger.getClass() == Player.class) ? 0 : -1;
            battle.setPriority(priority);
            battle.addText(
                    String.format(
                            "%s dodges %s's attack!",
                            dodger.getName(),
                            attacker.getName()));
        } else {
            // apply damage if not
            battle.addText(
                    String.format(
                            "%s fails to dodge %s's attack.",
                            dodger.getName(),
                            attacker.getName()));
            applyAttackDamage(battle, attacker, dodger);
        }
    }

    private void handleAttackSpell(
            Battle battle, Entity attacker, Entity caster) {
        // calculate damage for attacker
        // apply damage to caster
        // apply spell effect to attacker if no active effects
    }

    private void handleDefendDefend(
            Battle battle, Entity defender1, Entity defender2) {
        // do nothing?
    }

    private void handleDefendAttack(
            Battle battle, Entity defender, Entity attacker) {
        // calculate damage for defender and halve
        // apply damage to defender
    }

    private void handleDefendDodge(
            Battle battle, Entity defender, Entity dodger) {
        // do not roll for dodge
        // give priority to defender
    }

    private void handleDodgeDodge(
            Battle battle, Entity dodger1, Entity dodger2) {
        // do nothing?
    }

    private void handleDefendSpell(
            Battle battle, Entity defender, Entity caster) {
        // apply spell on defender with halved duration
    }

    private void handleDodgeSpell(
            Battle battle, Entity dodger, Entity caster) {
        // apply spell on dodger
    }

    private void handleSpellSpell(
            Battle battle, Entity caster1, Entity caster2) {
        // apply spell on caster 2 if no active effects
        // apply spell on caster 1 if no active effects
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
