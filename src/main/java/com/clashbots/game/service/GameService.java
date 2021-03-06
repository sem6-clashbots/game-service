package com.clashbots.game.service;

import com.clashbots.game.entity.Game;
import com.clashbots.game.entity.GameStatus;
import com.clashbots.game.entity.Side;
import com.clashbots.game.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    public Game saveGame(Game game) {
        log.info("inside save game method of GameService");
        return gameRepository.save(game);
    }

    public List<Game> findGames() {
        log.info("inside find game by id method of GameService");
        return gameRepository.findAll();
    }

    public List<Game> findGamesByStatus(GameStatus status) {
        log.info("inside find game by id method of GameService");
        return gameRepository.findByStatus(status);
    }

    public Optional<Game> findGame(UUID id) {
        log.info("inside find game by id method of GameService");
        return gameRepository.findById(id);
    }

    public List<Game> findGameSearching(Side userSide) {
        log.info("inside search opponent of GameService");

        if (userSide == Side.HUMANS) {
            return gameRepository.findRobotOpponent();
        } else if (userSide == Side.ROBOTS) {
            return gameRepository.findHumanOpponent();
        } else {
            return null;
        }
    }

    public List<Game> findStartingGames() {
        log.info("inside find starting games gservice");
        return gameRepository.findStartingGames();
    }

    public List<Game> findInProgressGames() {
        log.info("inside find in progress games in gservice");
        return gameRepository.findInProgressGames();
    }

    public void reset(){
        gameRepository.deleteAll();
    }
}
