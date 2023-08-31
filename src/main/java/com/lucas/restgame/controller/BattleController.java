package com.lucas.restgame.controller;

import com.lucas.restgame.entity.Battle;
import com.lucas.restgame.model.BattleAction;
import com.lucas.restgame.model.BattleManager;
import com.lucas.restgame.repository.BattleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class BattleController {

    @Autowired
    private BattleRepository battleRepository;

    private BattleManager battleManager;

    @PostMapping("/battles/{id}")
    // TODO remove debugging exception
    public Battle performBattleAction(
            @RequestBody String battleID, BattleAction action) {
        Battle battle = battleRepository.getBattleByID(battleID);
        if (battle == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                            "Battle with ID %s does not exist",
                            battleID
                    )
            );
        }
        Battle updatedBattle = battleManager.simulateBattle(battle, action);
        // should updateBattle return a Battle?
        battleRepository.updateBattle( battleID, updatedBattle);
        assert (updatedBattle == battle);
        return updatedBattle;
    }

    @GetMapping("/battles")
    public List<Battle> getAllBattles(
            @RequestParam(value = "active", defaultValue = "false")
            boolean active) {
        if (active) {
            return battleRepository.getActiveBattles();
        } else {
            return battleRepository.getAllBattles();
        }
    }

    @GetMapping("/battle-instances/{id}")
    public Battle getBattleByID(@PathVariable("id") String battleID) {
        return battleRepository.getBattleByID(battleID);
    }

    @PostMapping("/battle-instances")
    public Battle newBattle(@RequestBody Battle battle) {
        return battleRepository.saveBattle(battle);
    }

    @DeleteMapping("/battle-instances/{id}")
    public Boolean deleteBattleByID(@PathVariable("id") String battleID) {
        return battleRepository.deleteBattleByID(battleID);
    }

    @PutMapping("/battle-instances/{id}")
    public String updateBattle(
            @PathVariable("id") String battleID,
            @RequestBody Battle battle) {
        return battleRepository
                .updateBattle(battleID, battle);
    }
}
