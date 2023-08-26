package com.lucas.restgame;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class GameTests {

    @Test
    /*
    Given I have an id and a desc
    When I create a Game record with these parameters
    Then it should not throw an exception
     */
    public void gameContainsIdAndDesc() {
        assertDoesNotThrow(
            () ->  new Game(123, "This is a test Game")
        );
    }
}
