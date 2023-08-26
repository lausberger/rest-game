package com.lucas.restgame;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTests {

    @Autowired
    protected MockMvc mvc;

    @Test
    /*
    Given I query /game
    When I check the result "desc"
    I should see the default game id
     */
    public void getDescWithoutParam() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/game")
            .accept(MediaType.APPLICATION_JSON))
            .andReturn();
        JSONObject responseData = new JSONObject(result.getResponse().getContentAsString());
        assertEquals("Hello, player of game -1", responseData.get("desc"));
    }

    @Test
    /*
    Given I query /game with id parameter 123
    When I check the result "desc"
    Then it should contain the id param
     */
    public void getGameDescWithParam() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/game")
            .queryParam("id", String.valueOf(123))
            .accept(MediaType.APPLICATION_JSON))
            .andReturn();
        JSONObject responseData = new JSONObject(result.getResponse().getContentAsString());
        assertEquals("Hello, player of game 123", responseData.get("desc"));
    }

    @Test
    /*
    Given I query /game multiple times
    When I check the results' "id"
    Then they should not be the same
     */
    public void getIdChangesBetweenQueries() throws Exception {
        MvcResult result1 = mvc.perform(MockMvcRequestBuilders.get("/game")
            .accept(MediaType.APPLICATION_JSON))
            .andReturn();
        MvcResult result2 = mvc.perform(MockMvcRequestBuilders.get("/game")
            .accept(MediaType.APPLICATION_JSON))
            .andReturn();
        JSONObject result1Data = new JSONObject(result1.getResponse().getContentAsString());
        JSONObject result2Data = new JSONObject(result2.getResponse().getContentAsString());
        assertNotEquals(result1Data.get("id"), result2Data.get("id"));
    }
}
