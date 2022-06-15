package com.clashbots.game.controller;

//import com.clashbots.comment.RabbitmqSender;

import com.clashbots.game.entity.*;
import com.clashbots.game.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.handler.annotation.MessageMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@EnableScheduling
@RequestMapping("/games")
@CrossOrigin(origins = "https://cb-game-platform.herokuapp.com/")
//@CrossOrigin(origins = "http://localhost:3000/")
public class GameController {
    @Autowired
    private GameService gameService;

    /*Get one by ID*/
    @GetMapping("/")
    public ResponseEntity<List<Game>> findGames() {
        log.info("inside find game by id method of GameController");
        return new ResponseEntity<>(gameService.findGames(), HttpStatus.OK);
    }

    /*Get one by ID*/
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Game>> findGameById(@PathVariable("id") UUID gameId) {
        log.info("inside find game by id method of GameController");
        return new ResponseEntity<>(gameService.findGame(gameId), HttpStatus.OK);
    }

    /*Get by Status*/
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Game>> findByStatus(@PathVariable("status") GameStatus status) {
        log.info("inside find game by id method of GameController");
        return new ResponseEntity<>(gameService.findGamesByStatus(status), HttpStatus.OK);
    }

    /*Get one by ID*/
    @DeleteMapping("/reset")
    public ResponseEntity<String> reset() {
        log.info("inside find game by id method of GameController");
        gameService.reset();
        return new ResponseEntity<>("RESET", HttpStatus.OK);
    }

    /* START||FIND GAME*/
    @PostMapping("/start")
    public ResponseEntity<Game> startFindGame(@RequestBody Init init) {
        log.info("inside START game method of GameController");
        //try to find a game
        List<Game> games = gameService.findGameSearching(init.getUserSide());
        Game game;

        if (games.size() > 0) {
            //found game
            game = games.get(0);
            log.info("found: " + game);
            game.setPlayer(init.getUserAddress(), init.getUserSide());
            game.setStatus(GameStatus.STARTING);
        } else {
            //make game
            log.info("nothing found - creating new game");
            game = new Game();
            game.setPlayer(init.getUserAddress(), init.getUserSide());
            game.setStatus(GameStatus.SEARCHING);
        }

        return new ResponseEntity<>(gameService.saveGame(game), HttpStatus.OK);
    }

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/start")
    public void start(String gameId) throws Exception {
        try {
            simpMessagingTemplate.convertAndSendToUser(gameId, "/start", gameId);
        } catch (Exception e) {
            System.out.println("Error in start ws: " + e);
        }
    }

    @MessageMapping("/move")
    public void move(Message message) throws Exception {
        try {
            Game game = gameService.findGame(message.getGameId()).stream().findFirst().orElse(null);
            if (game != null) {
                game.setMove(message.getSenderAddress(), message.getMove());
                gameService.saveGame(game);
            }
        } catch (Exception e) {
            System.out.println("Error in start ws: " + e);
        }
    }

    @Scheduled(fixedRate = 10000)
    //check insta changes after change
    private void timer() {
        //check all PICKING status games and set to PLAY OUT
        this.handleEndRound();
        //check all STARTING status games and set to PICKING
        this.handleSync();
        //check all FINISH status games and send result to players? maybe not
//        this.handleFinish();
    }

    private void handleSync() {
        //SYNC GAMES
        List<Game> startingGames = gameService.findStartingGames();
        System.out.println("Starting Games: " + startingGames.size());
        startingGames.forEach((game) -> {
            System.out.println(game);
            game.setStatus(GameStatus.IN_PROGRESS);
            gameService.saveGame(game);
            simpMessagingTemplate.convertAndSendToUser(game.getId().toString(), "/sync", game);
        });
    }

    private Side checkWinner(Move hm, Move rm) {
        if (hm == rm) {
            //SAME MOVE
            return null;
        } else if (hm != Move.WAIT && rm == Move.WAIT) {
            //Robots only didnt pick
            return Side.HUMANS;
        } else if (hm == Move.WAIT && rm != Move.WAIT) {
            //Humans only didnt pick
            return Side.ROBOTS;
        } else if (hm == Move.SWORD && rm == Move.HAMMER || hm == Move.HAMMER && rm == Move.SHIELD || hm == Move.SHIELD && rm == Move.SWORD) {
            return Side.HUMANS;
        }

        return Side.ROBOTS;
    }

    private void handleEndRound() {
        //END ROUND GAMES
        List<Game> inProgressGames = gameService.findInProgressGames();
        System.out.println("In Progress Games: " + inProgressGames.size());
        inProgressGames.forEach((game) -> {
            Move hm = game.getHuman_move();
            Move rm = game.getRobot_move();
            Side winner = this.checkWinner(hm, rm);
            game.endRound(winner);

            simpMessagingTemplate.convertAndSendToUser(game.getId().toString(), "/move", game);
            game.setHuman_move(Move.WAIT);
            game.setRobot_move(Move.WAIT);
            gameService.saveGame(game);
        });
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
