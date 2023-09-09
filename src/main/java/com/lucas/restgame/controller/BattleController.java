package com.lucas.restgame.controller;

import com.lucas.restgame.entity.Battle;
import com.lucas.restgame.model.BattleAction;
import com.lucas.restgame.model.BattleManager;
import com.lucas.restgame.model.BattleRequest;
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

    // TODO this makes more sense as a PUT!
    /*
    it's totally non-RESTful, but battle lookup based on Player/auth token
    instead of battleID could be more secure and make combat easier
     */
    @PostMapping("/battles/{id}")
    public Battle performBattleAction(
            @PathVariable("id") String battleID,
            @RequestBody BattleRequest request) {
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
        BattleManager battleManager = new BattleManager(battle);
        BattleAction action = request.getAction();
        int target = request.getTarget(); // unused for now
        Battle updatedBattle = battleManager.performTurn(action);
        // should updateBattle return a Battle?
        battleRepository.updateBattle(battleID, updatedBattle);
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

    @GetMapping("/battles/{id}")
    public Battle getBattleByID(@PathVariable("id") String battleID) {
        return battleRepository.getBattleByID(battleID);
    }

    @PostMapping("/battles")
    public Battle createBattle(@RequestBody Battle battle) {
        return battleRepository.createBattle(battle);
    }

    @DeleteMapping("/battles/{id}")
    public Boolean deleteBattleByID(@PathVariable("id") String battleID) {
        return battleRepository.deleteBattleByID(battleID);
    }

    @PutMapping("/battles/{id}")
    public String updateBattle(
            @PathVariable("id") String battleID,
            @RequestBody Battle battle) {
        return battleRepository
                .updateBattle(battleID, battle);
    }
}
