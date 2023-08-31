package com.lucas.restgame;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.lucas.restgame.entity.Battle;
import com.lucas.restgame.entity.Enemy;
import com.lucas.restgame.entity.Player;
import com.lucas.restgame.model.BattleStatus;
import com.lucas.restgame.repository.BattleRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = {
        "amazon.dynamodb.endpoint=http://localhost:8000/",
        "amazon.aws.accesskey=key",
        "amazon.aws.secretkey=secretkey"
})
public class BattleRepositoryTests {

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    BattleRepository battleRepository;

    @Before
    public void setup() throws Exception {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        CreateTableRequest tableRequest = dynamoDBMapper
                .generateCreateTableRequest(Battle.class);
        tableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L)
        );
        amazonDynamoDB.createTable(tableRequest);
    }

    // TODO clear table after each test if conflicts arise

    @After
    public void teardown() throws Exception {
        amazonDynamoDB.deleteTable(dynamoDBMapper
                .generateDeleteTableRequest(Battle.class));
    }

    @Test
    /*
    Given I have created a new Battle
    When I save it to the repository
    Then it should not raise an exception
     */
    public void addBattleToTable() throws AssertionError {
        Battle battle = new Battle();
        assertDoesNotThrow(() -> battleRepository.createBattle(battle));
    }

    @Test
    /*
    Given I create a Battle
    When it has been saved to the repository
    Then I should be able to find it by its ID
    */
    public void findBattleInTable() throws AssertionError {
        Battle battle = battleRepository.createBattle(new Battle());
        Battle loadedBattle = battleRepository.getBattleByID(battle.getBattleID());
        assertNotNull(loadedBattle);
    }

    @Test
    /*
    Given there is a Battle in the table
    When I load it, change a value, and save
    Then I should see the changes reflected in the table entry
    */
    public void updateBattleInTable() throws AssertionError {
        Battle localBattle = battleRepository.createBattle(
                new Battle(new Player(), new Enemy())
        );
        Battle oldBattle = battleRepository.getBattleByID(localBattle.getBattleID());
        localBattle.getPlayer().setHealth(90);
        battleRepository.updateBattle(localBattle.getBattleID(), localBattle);
        Battle newBattle = battleRepository.getBattleByID(localBattle.getBattleID());
        int oldHealth = oldBattle.getPlayer().getHealth();
        int newHealth = newBattle.getPlayer().getHealth();
        assertNotEquals(oldHealth, newHealth);
    }

    @Test
    /*
    Given I have saved a Battle to the table
    When I delete the Battle from the table
    Then I should not be able to find it again
     */
    public void deleteBattleInTable() throws AssertionError {
        Battle battle = battleRepository.createBattle(new Battle());
        battleRepository.deleteBattleByID(battle.getBattleID());
        Battle loadedBattle = battleRepository.getBattleByID(battle.getBattleID());
        assertNull(loadedBattle);
    }

    @Test
    /*
    Given I have added several Battles to the table
    When I scan the table contents
    Then I should see all the Battles
     */
    public void getAllBattlesInTable() throws AssertionError {
        HashMap<String, Boolean> battleIDMap = new HashMap<String, Boolean>();
        for (int i = 0; i < 5; i++) {
            Battle battle = battleRepository.createBattle(new Battle());
            battleIDMap.put(battle.getBattleID(), true);
        }
        List<Battle> battlesList = battleRepository.getAllBattles();
        for (Battle battle : battlesList) {
            assertTrue(battleIDMap.containsKey(battle.getBattleID()));
        }
    }

    @Test
    /*
    Given I have saved several active and inactive Battles
    When I scan the table contents for active Battles
    Then I should only see Battles where active is true
     */
    public void getAllActiveBattlesInTable() throws AssertionError {
        for (int i = 0; i < 6; i++) {
            Battle battle = new Battle();
            if (i % 2 == 0) {
                battle.setStatus(BattleStatus.VICTORY);
            }
            battleRepository.createBattle(battle);
        }
        List<Battle> activeBattles = battleRepository.getActiveBattles();
        for (Battle battle : activeBattles) {
            assertEquals(battle.getStatus(), BattleStatus.ONGOING);
        }
    }
}
