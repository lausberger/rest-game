package com.lucas.restgame;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.lucas.restgame.entity.Game;
import com.lucas.restgame.repository.GameRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = {
    "amazon.dynamodb.endpoint=http://localhost:8080/",
    "amazon.aws.accesskey=key",
    "amazon.aws.secretkey=secretkey"
})
public class GameRepositoryTests {

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    GameRepository gameRepository;

    @Before
    public void setup() throws Exception {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableRequest = dynamoDBMapper
            .generateCreateTableRequest(Game.class);
        tableRequest.setProvisionedThroughput(
            new ProvisionedThroughput(1L, 1L)
        );
        amazonDynamoDB.createTable(tableRequest);
    }

    @After
    public void teardown() throws Exception {
        amazonDynamoDB.deleteTable(dynamoDBMapper
            .generateDeleteTableRequest(Game.class));
    }

    @Test
    /*
    Given I have created a new Game
    When I save it to the repository
    Then it should not raise an exception
     */
    public void addGameToTable() throws AssertionError {
        Game game = new Game();
        assertDoesNotThrow(() -> gameRepository.saveGame(game));
    }

    @Test
    /*
    Given I create a Game
    When it has been saved to the repository
    Then I should be able to find it by its ID
    */
    public void findGameInTable() throws AssertionError {
        Game game = gameRepository.saveGame(new Game());
        Game loadedGame = gameRepository.getGameByID(game.getGameID());
        assertNotNull(loadedGame);
    }

    @Test
    /*
    Given there is a Game in the table
    When I load it, change a value, and save
    Then I should see the changes reflected in the table entry
    */
    public void updateGameInTable() throws AssertionError {
        Game localGame = gameRepository.saveGame(new Game(100, 100, 1, true));
        Game oldGame = gameRepository.getGameByID(localGame.getGameID());
        localGame.setPlayerHealth(90);
        gameRepository.updateGame(localGame.getGameID(), localGame);
        Game newGame = gameRepository.getGameByID(localGame.getGameID());
        assertNotEquals(oldGame.getPlayerHealth(), newGame.getPlayerHealth());
    }

    @Test
    /*
    Given I have saved a Game to the table
    When I delete the Game from the table
    Then I should not be able to find it again
     */
    public void deleteGameInTable() throws AssertionError {
        Game game = gameRepository.saveGame(new Game());
        gameRepository.deleteGameByID(game.getGameID());
        Game loadedGame = gameRepository.getGameByID(game.getGameID());
        assertNull(loadedGame);
    }
}
