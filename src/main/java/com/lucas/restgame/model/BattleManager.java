package com.lucas.restgame.model;

import com.lucas.restgame.entity.Battle;
import com.lucas.restgame.entity.Enemy;
import com.lucas.restgame.entity.Player;

import java.util.List;

public class BattleManager {

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
        BattleAction enemyAction = enemy.getAction();

        // simulate dodges
        if (enemyAction == BattleAction.DODGE) {
            enemyDodged = attemptDodge();
        }
        if (playerAction == BattleAction.DODGE) {
            playerDodged = attemptDodge();
        }

        // calculate base attack damage
        if (enemyAction == BattleAction.ATTACK) {
            enemyHit = enemy.getPower() - player.getDefense();
        }
        if (playerAction == BattleAction.ATTACK) {
            playerHit = player.getPower() - enemy.getDefense();
        }

        // factor in defends and dodges
        if (enemyAction == BattleAction.DEFEND) {
            if (playerAction == BattleAction.ATTACK) {
                playerHit = Math.round(playerHit * 0.5f);
            } else if (playerAction == BattleAction.DODGE) {
                // Defend -> Dodge guarantees defender priority
                battle.setPriority(1);
            }
        } else if (enemyDodged) {
            playerHit = 0;
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
        }

        // determine turn order
        boolean playerMovesFirst;
        if (battle.getPriority() == -1) {
            // random damage order
            playerMovesFirst = Math.random() < 0.5f;
        } else {
            playerMovesFirst = battle.getPriority() == 0;
        }

        // apply damage
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
                // enemy dodges
                if (enemyDodged) {
                    battle.addText(
                            String.format(
                                    "%s dodges %s's attack!",
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
                    // player dodges
                    if (playerDodged) {
                        battle.addText(
                                String.format(
                                        "%s dodges %s's attack!",
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
                }
            }
        }
        return battle;
    }

    // placeholder dodge logic
    private boolean attemptDodge() {
        return Math.random() < 0.5;
    }
}
