package com.clashbots.game.controller;

//import com.clashbots.comment.RabbitmqSender;
import com.clashbots.game.entity.Game;
import com.clashbots.game.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
@Slf4j
public class GameController {
    @Autowired
    private GameService gameService;

    @PostMapping("/")
    public Game saveGame(@RequestBody Game game){
        log.info("inside save game method of GameController");
        return gameService.saveGame(game);
    }

    @GetMapping("/{id}")
    public Game findGameById(@PathVariable("id") Long gameId){
        log.info("inside find game by id method of GameController");
        return gameService.findGame(gameId);
    }

//    private RabbitmqSender rabbitMqSender;
//    @Autowired
//    public CommentController(RabbitmqSender rabbitMqSender) {
//        this.rabbitMqSender = rabbitMqSender;
//    }

//    @GetMapping("/rabbitmq/contract/{id}")
//    public String testRabbitmqContract(@PathVariable("id") Long userId) {
//        log.info("inside find user by id with contract method of UserController");
//        rabbitMqSender.send(userId);
//        return "Message has been sent Successfully: " + userId;
//    }
}
