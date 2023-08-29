package com.lucas.restgame.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.lucas.restgame.entity.Game;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GameRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

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
