package com.lucas.restgame;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GameController {
    private static final String template = "Hello, player of game %s";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/game")
    public Game game(@RequestParam(value = "id", defaultValue = "-1") long id) {
        return new Game(counter.incrementAndGet(), String.format(template, id));
    }
}
