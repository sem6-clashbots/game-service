package com.clashbots.game.controller;

//import com.clashbots.comment.RabbitmqSender;

import com.clashbots.game.entity.*;
import com.clashbots.game.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/games")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class GameController {
    @Autowired
    private GameService gameService;

    /* START||FIND GAME*/
    @PostMapping("/start")
    public ResponseEntity<Game> startFindGame(@RequestBody Init init) {
        log.info("inside START game method of GameController");
        //try to find a game
        List<Game> games = gameService.findGameSearching(init.getUserSide());
        Game game;

        if(games.size() > 0){
            //found game
            game = games.get(0);
            log.info("found: " + game);
            game.setPlayer(init.getUserAddress(), init.getUserSide());
            game.setStatus(GameStatus.IN_PROGRESS);
        }else{
            //make game
            log.info("nothing found - creating new game");
            game = new Game();
            game.setPlayer(init.getUserAddress(), init.getUserSide());
            game.setStatus(GameStatus.SEARCHING);
        }

        return new ResponseEntity<>(gameService.saveGame(game), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Game>> findGameById(@PathVariable("id") UUID gameId) {
        log.info("inside find game by id method of GameController");
        return new ResponseEntity<>(gameService.findGame(gameId), HttpStatus.OK);
    }

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/start")
    @CrossOrigin(origins = "http://localhost:3000")
    public Message start(Message message) throws Exception {
        System.out.println("sending msg: " + message.getMessage());
        System.out.println("On topic: " + message.getGameId().toString());
        simpMessagingTemplate.convertAndSendToUser(message.getGameId().toString(), "/start", message);
        return message;
    }

    @MessageMapping("/move")
    @CrossOrigin(origins = "http://localhost:3000")
    public Message move(Message message) throws Exception {
        System.out.println("sending msg: " + message.getMessage());
        System.out.println("On topic: " + message.getGameId().toString());
        simpMessagingTemplate.convertAndSendToUser(message.getGameId().toString(), "/start", message);
        return message;
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
