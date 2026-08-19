package in.cricket.cricketCardGame.service;

import in.cricket.cricketCardGame.entity.Cricketer;
import in.cricket.cricketCardGame.entity.Game;
import in.cricket.cricketCardGame.entity.GameCard;
import in.cricket.cricketCardGame.entity.GamePlayer;
import in.cricket.cricketCardGame.enums.Skill;
import in.cricket.cricketCardGame.repository.CricketerRepository;
import in.cricket.cricketCardGame.repository.GameCardRepository;
import in.cricket.cricketCardGame.repository.GamePlayerRepository;
import in.cricket.cricketCardGame.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final CricketerRepository cricketerRepository;
    private final GameCardRepository gameCardRepository;
    private final GamePlayerRepository gamePlayerRepository;

    public GameService(
            GameRepository gameRepository,
            CricketerRepository cricketerRepository,
            GamePlayerRepository gamePlayerRepository,
            GameCardRepository gameCardRepository) {

        this.gameRepository = gameRepository;
        this.cricketerRepository = cricketerRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.gameCardRepository = gameCardRepository;
    }


    // =========================================================
    // CREATE GAME
    // =========================================================

    public Game createGame(List<String> playerNames) {

        Game game = new Game();

        game.setNumberOfPlayers(playerNames.size());
        game.setCurrentRound(1);

        // Player 1 starts playing cards
        game.setCurrentTurn(1);

        game.setStatus("WAITING");

        List<GamePlayer> gamePlayers = new ArrayList<>();

        for (String name : playerNames) {

            GamePlayer player = new GamePlayer();

            player.setPlayerName(name);
            player.setScore(0);
            player.setGame(game);

            gamePlayers.add(player);
        }

        game.setPlayers(gamePlayers);

        return gameRepository.save(game);
    }


    // =========================================================
    // START GAME
    // =========================================================

    public Game startGame(int gameId) {

        Game game = gameRepository.getReferenceById(gameId);

        // Game already started
        if (!game.getStatus().equals("WAITING")) {
            return game;
        }

        // Get all cricketers
        List<Cricketer> cricketers =
                cricketerRepository.findAll();

        // Randomize cards
        Collections.shuffle(cricketers);

        List<GamePlayer> gamePlayers =
                gamePlayerRepository.findByGameId(gameId);

        int index = 0;

        // Give 10 random cards to every player
        for (GamePlayer player : gamePlayers) {

            for (int i = 0; i < 10; i++) {

                Cricketer cricketer =
                        cricketers.get(index);

                GameCard card = new GameCard();

                card.setGame(game);
                card.setPlayer(player);
                card.setCricketer(cricketer);

                card.setUsed(false);
                card.setPlayedRound(0);

                player.getCards().add(card);

                index++;
            }
        }

        game.setStatus("PLAYING");

        return gameRepository.save(game);
    }


    // =========================================================
    // CHOOSE SKILL
    // =========================================================

    public Game chooseSkill(
            int gameId,
            int playerId,
            Skill skill) {

        Game game =
                gameRepository.getReferenceById(gameId);

        // Game already over
        if (game.getStatus().equals("GAME_OVER")) {
            return game;
        }

        // Skill already selected for this round
        if (game.getCurrentSkill() != null) {
            return game;
        }

        List<GamePlayer> players =
                gamePlayerRepository.findByGameId(gameId);

        /*
         * Skill chooser alternates every round.
         *
         * Round 1 -> Player 1
         * Round 2 -> Player 2
         * Round 3 -> Player 1
         * Round 4 -> Player 2
         *
         * Formula:
         *
         * (currentRound - 1) % numberOfPlayers
         */

        int skillChooserIndex =
                (game.getCurrentRound() - 1)
                        % game.getNumberOfPlayers();

        GamePlayer skillChooser =
                players.get(skillChooserIndex);

        // Wrong player trying to choose skill
        if (skillChooser.getId() != playerId) {
            return game;
        }

        // Set selected skill
        game.setCurrentSkill(skill);

        return gameRepository.save(game);
    }


    // =========================================================
    // PLAY CARD
    // =========================================================

    public Game playCard(
            int gameId,
            int playerId,
            int cardId) {

        Game game =
                gameRepository.getReferenceById(gameId);

        // Skill must be selected first
        if (game.getCurrentSkill() == null) {
            return game;
        }

        List<GamePlayer> players =
                gamePlayerRepository.findByGameId(gameId);

        /*
         * currentTurn controls card playing.
         *
         * 1 -> Player 1
         * 2 -> Player 2
         */

        GamePlayer currentPlayer =
                players.get(game.getCurrentTurn() - 1);

        // Wrong player trying to play
        if (currentPlayer.getId() != playerId) {
            return game;
        }

        GameCard card =
                gameCardRepository.getReferenceById(cardId);

        /*
         * Card must:
         *
         * 1. Belong to this game
         * 2. Belong to this player
         * 3. Not already be used
         */

        if (card.getGame().getId() == gameId
                && card.getPlayer().getId() == playerId
                && !card.isUsed()) {

            // Mark card as used
            card.setUsed(true);

            // Remember which round the card was played
            card.setPlayedRound(
                    game.getCurrentRound()
            );

            gameCardRepository.save(card);


            // =================================================
            // MOVE TO NEXT PLAYER
            // =================================================

            if (game.getCurrentTurn()
                    < game.getNumberOfPlayers()) {

                /*
                 * Player 1 played.
                 * Now Player 2 plays.
                 */

                game.setCurrentTurn(
                        game.getCurrentTurn() + 1
                );

            } else {

                /*
                 * Last player has played.
                 * Round is now complete.
                 */

                calculateWinner(game, players);


                // =================================================
                // GAME OVER AFTER ROUND 10
                // =================================================

                if (game.getCurrentRound() == 10) {

                    GamePlayer finalWinner =
                            calculateFinalWinner(players);

                    game.setWinner(
                            finalWinner.getPlayerName()
                    );

                    game.setStatus("GAME_OVER");

                    // No skill after game ends
                    game.setCurrentSkill(null);

                }


                // =================================================
                // START NEXT ROUND
                // =================================================

                else {

                    game.setCurrentRound(
                            game.getCurrentRound() + 1
                    );

                    /*
                     * Every round starts card playing
                     * from Player 1.
                     */

                    game.setCurrentTurn(1);

                    /*
                     * Skill has to be selected again.
                     */

                    game.setCurrentSkill(null);
                }
            }
        }

        return gameRepository.save(game);
    }


    // =========================================================
    // CALCULATE ROUND WINNER
    // =========================================================

    private void calculateWinner(
            Game game,
            List<GamePlayer> players) {

        int maxValue = Integer.MIN_VALUE;
        int winnerCount = 0;
        GamePlayer winner = null;

        for (GamePlayer player : players) {

            GameCard playedCard = null;

            for (GameCard card : player.getCards()) {

                if (card.isUsed()
                        && card.getPlayedRound() == game.getCurrentRound()) {

                    playedCard = card;
                    break;
                }
            }

            if (playedCard == null) {
                continue;
            }

            int value = 0;

            switch (game.getCurrentSkill()) {

                case BATTING:
                    value = playedCard.getCricketer().getBatting();
                    break;

                case BOWLING:
                    value = playedCard.getCricketer().getBowling();
                    break;

                case FIELDING:
                    value = playedCard.getCricketer().getFielding();
                    break;

                case KEEPING:
                    value = playedCard.getCricketer().getKeeping();
                    break;
            }

            if (value > maxValue) {

                maxValue = value;
                winner = player;
                winnerCount = 1;

            } else if (value == maxValue) {

                winnerCount++;
            }
        }

        // If highest values are equal,
        // nobody gets a point.
        if (winnerCount == 1 && winner != null) {

            winner.setScore(
                    winner.getScore() + 1
            );
        }
    }

    // =========================================================
    // CALCULATE FINAL WINNER
    // =========================================================

    private GamePlayer calculateFinalWinner(List<GamePlayer> players) {

        int maxScore = Integer.MIN_VALUE;

        for (GamePlayer player : players) {
            if (player.getScore() > maxScore) {
                maxScore = player.getScore();
            }
        }

        // Count players having the highest score
        int highestScoreCount = 0;

        for (GamePlayer player : players) {
            if (player.getScore() == maxScore) {
                highestScoreCount++;
            }
        }

        // Tie -> subtract 1 from everyone's score
        if (highestScoreCount > 1) {

            for (GamePlayer player : players) {

                player.setScore(
                        Math.max(0, player.getScore() - 1)
                );
            }

            return null;
        }

        // Only one player has the highest score
        for (GamePlayer player : players) {

            if (player.getScore() == maxScore) {
                return player;
            }
        }

        return null;
    }


    // =========================================================
    // GET GAME
    // =========================================================

    public Game getGame(int gameId) {

        return gameRepository.getReferenceById(gameId);
    }
}