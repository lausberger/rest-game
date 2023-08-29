package com.lucas.restgame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucas.restgame.entity.Game;
import com.lucas.restgame.repository.GameRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class GameControllerTests {

    @MockBean
    private GameRepository gameRepository;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    /*
    Given there is a Game with ID "xyz" in the repository
    When I send a GET request to /games/xyz
    Then the response should contain said Game
     */
    public void getRequestWithID() throws Exception {
        Game game = new Game();
        game.setGameID("xyz");

        when(gameRepository.getGameByID("xyz")).thenReturn(game);
        MvcResult result = mvc.perform(get("/games/xyz")
            .accept(MediaType.APPLICATION_JSON))
            .andReturn();

        JSONObject responseData = new JSONObject(result.getResponse().getContentAsString());
        assertEquals("xyz", responseData.get("gameID"));
    }

    @Test
    /*
    Given I have created a Game
    When I send a POST request to /games
    Then the response should contain said Game
     */
    public void postRequestWithGame() throws Exception {
        Game game = new Game();
        game.setGameID("abc");

        MvcResult result = mvc.perform(post("/games")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(game)))
            .andReturn();

        JSONObject responseData = new JSONObject(result.getResponse().getContentAsString());
        assertEquals(responseData.get("gameID"), game.getGameID());
    }
}
