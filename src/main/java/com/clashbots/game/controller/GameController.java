package com.clashbots.game.controller;

//import com.clashbots.comment.RabbitmqSender;
import com.clashbots.game.entity.Game;
import com.clashbots.game.entity.Move;
import com.clashbots.game.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.util.HtmlUtils;

@RestController
@RequestMapping("/games")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class GameController {
    @Value("${server.port}")
    private String port;

    @Autowired
    private GameService gameService;

    @PostMapping("/")
    public Game saveGame(@RequestBody Game game){
        log.info("inside save game method of GameController");
        return gameService.saveGame(game);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> findGameById(@PathVariable("id") Long gameId){
        log.info("inside find game by id method of GameController");
        return new ResponseEntity<>(gameService.findGame(gameId), HttpStatus.OK);
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Move messageGame(Move move){
        return new Move("Hello, " + HtmlUtils.htmlEscape(move.getContent()) + "!");
    }
    @MessageMapping("/incoming")
    @SendTo("/topic/outgoing")
    public String incoming(Move move) {
        log.info("inside move game");
        return String.format("Application on port %s responded to your message: \"%s\"", port, move.getContent());
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
