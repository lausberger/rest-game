package com.lucas.restgame;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.lucas.restgame.entity.Battle;
        import com.lucas.restgame.model.BattleAction;
        import com.lucas.restgame.model.BattleRequest;
        import com.lucas.restgame.repository.BattleRepository;
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
public class BattleControllerTests {

    @MockBean
    private BattleRepository battleRepository;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    /*
    Given there is a Battle in the repo
    When I send a POST to /battles/xyz
    Then the response should contain said Battle
    And response status should not be 404 Not Found
     */
    public void performBattleActionWithValidID() throws Exception {
        Battle battle = new Battle();
        BattleRequest request = new BattleRequest();
        request.setAction(BattleAction.ATTACK);
        request.setTarget(0);

        when(battleRepository.getBattleByID("xyz"))
                .thenReturn(battle);
        MvcResult response = mvc.perform(post("/battles/xyz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        String responseString = response.getResponse().getContentAsString();
        Battle returnedBattle = objectMapper.readValue(responseString, Battle.class);
        assertSame(battle.getBattleID(), returnedBattle.getBattleID());
    }

    @Test
    /*
    Given there is NOT a battle with ID "xyz" in the repo
    When I send a POST to /battles/xyz
    Then I should receive a 404 Not Found response
    And it should say "Battle with ID xyz does not exist"
     */
    public void preformBattleActionWithoutValidID() throws Exception {
        BattleRequest request = new BattleRequest();
        request.setAction(BattleAction.ATTACK);
        request.setTarget(0);

        when(battleRepository.getBattleByID("xyz"))
                .thenReturn(null);
        mvc.perform(post("/battles/xyz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Battle with ID xyz does not exist"));
    }
}
