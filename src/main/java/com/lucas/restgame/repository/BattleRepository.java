package com.lucas.restgame.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.lucas.restgame.entity.Battle;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BattleRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    // TODO implement limit if battles table becomes arbitrarily large
    public List<Battle> getAllBattles() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Battle> scanResult = dynamoDBMapper.scan(Battle.class, scanExpression);
        return new ArrayList<Battle>(scanResult);
    }

    // TODO implement limit if battles table becomes arbitrarily large
    public List<Battle> getActiveBattles() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withBOOL(true));
        scanExpression.addFilterCondition("active", condition);
        PaginatedScanList<Battle> scanResult = dynamoDBMapper.scan(Battle.class, scanExpression);
        return new ArrayList<Battle>(scanResult);
    }

    public Battle saveBattle(Battle battle) {
        dynamoDBMapper.save(battle);
        return battle;
    }

    public Battle getBattleByID(String battleID) {
        return dynamoDBMapper.load(Battle.class, battleID);
    }

    // TODO conditional return value based on success
    public boolean deleteBattleByID(String battleID) {
        Battle battle = this.getBattleByID(battleID);
        dynamoDBMapper.delete(battle);
        return true;
    }

    public String updateBattle(String battleID, Battle battle) {
        dynamoDBMapper.save(battle,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("battleID",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(battleID)
                                )));
        return battleID;
    }
}
