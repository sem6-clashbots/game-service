package com.clashbots.game.service;

import com.clashbots.game.entity.Game;
import com.clashbots.game.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    public Game saveGame(Game notification) {
        log.info("inside save game method of GameService");
        return gameRepository.save(notification);
    }

    public Game findGame(Long notificationId) {
        log.info("inside find game by id method of GameService");
        return gameRepository.findByGameId(notificationId);
    }
}
