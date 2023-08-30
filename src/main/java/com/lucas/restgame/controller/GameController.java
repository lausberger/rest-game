package com.lucas.restgame.controller;

import com.lucas.restgame.entity.Game;
import com.lucas.restgame.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @GetMapping("/games")
    public List<Game> getAllGames(@RequestParam(value = "active", defaultValue = "false") boolean active) {
        if (active) {
            return gameRepository.getActiveGames();
        } else {
            return gameRepository.getAllGames();
        }
    }

    @GetMapping("/games/{id}")
    public Game getGameByID(@PathVariable("id") String gameID) {
        return gameRepository.getGameByID(gameID);
    }

    @PostMapping("/games")
    public Game newGame(@RequestBody Game game) {
        return gameRepository.saveGame(game);
    }

    @DeleteMapping("/games/{id}")
    public Boolean deleteGameByID(@PathVariable("id") String gameID) {
        return gameRepository.deleteGameByID(gameID);
    }

    @PutMapping("/games/{id}")
    public String updateGame(@PathVariable("id") String gameID, @RequestBody Game game) {
        return gameRepository.updateGame(gameID, game);
    }
}
