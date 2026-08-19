package in.cricket.cricketCardGame.controller;

import in.cricket.cricketCardGame.entity.Game;
import in.cricket.cricketCardGame.enums.Skill;
import in.cricket.cricketCardGame.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
public class GameController {

    private final GameService gameService ;
    public GameController(GameService gameService){
        this.gameService = gameService ;
    }

    //Create the game
    @PostMapping("/creategame")
    public Game createGame(@RequestBody List<String> playerNames) {
        return gameService.createGame(playerNames);
    }

    //Start the game
    @PostMapping("/startgame/{gameId}")
    public Game startGame(@PathVariable int gameId) {
        return gameService.startGame(gameId);
    }

    //Play the card
    @PostMapping("/play/{gameId}/{playerId}/{cardId}")
    public Game playCard(
            @PathVariable int gameId,
            @PathVariable int playerId,
            @PathVariable int cardId) {

        return gameService.playCard(gameId, playerId, cardId);
    }
   //Choose Skill
    @PostMapping("/choose-skill/{gameId}/{playerId}")
    public Game chooseSkill(
            @PathVariable int gameId,
            @PathVariable int playerId,
            @RequestBody Skill skill) {

        return gameService.chooseSkill(gameId, playerId, skill);
    }

    @GetMapping("/{gameId}")
    public Game getGame(@PathVariable int gameId) {
        return gameService.getGame(gameId);
    }

}
