package com.lucas.restgame.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.lucas.restgame.entity.Game;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GameRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    // TODO implement limit if games table becomes arbitrarily large
    public List<Game> getAllGames() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Game> scanResult = dynamoDBMapper.scan(Game.class, scanExpression);
        return new ArrayList<Game>(scanResult);
    }

    // TODO implement limit if games table becomes arbitrarily large
    public List<Game> getActiveGames() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
            .withAttributeValueList(new AttributeValue().withBOOL(true));
        scanExpression.addFilterCondition("active", condition);
        PaginatedScanList<Game> scanResult = dynamoDBMapper.scan(Game.class, scanExpression);
        return new ArrayList<Game>(scanResult);
    }

    public Game saveGame(Game game) {
        dynamoDBMapper.save(game);
        return game;
    }

    public Game getGameByID(String gameID) {
        return dynamoDBMapper.load(Game.class, gameID);
    }

    // TODO conditional return value based on success
    public boolean deleteGameByID(String gameID) {
        Game game = this.getGameByID(gameID);
        dynamoDBMapper.delete(game);
        return true;
    }

    public String updateGame(String gameID, Game game) {
        dynamoDBMapper.save(game,
            new DynamoDBSaveExpression()
                .withExpectedEntry("gameID",
                    new ExpectedAttributeValue(
                        new AttributeValue().withS(gameID)
                    )));
        return gameID;
    }
}
