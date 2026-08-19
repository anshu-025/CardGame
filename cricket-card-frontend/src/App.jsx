import { useState } from "react";
import "./App.css";

const API_URL =  import.meta.env.VITE_API_URL || "http://localhost:8080" ;

function App() {
  const [player1, setPlayer1] = useState("");
  const [player2, setPlayer2] = useState("");

  const [game, setGame] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // --------------------------------------------------
  // CREATE GAME
  // --------------------------------------------------

  const createGame = async () => {
    if (!player1.trim() || !player2.trim()) {
      setError("Please enter both player names.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await fetch(`${API_URL}/games/creategame`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify([
          player1.trim(),
          player2.trim(),
        ]),
      });

      if (!response.ok) {
        throw new Error("Could not create game.");
      }

      const data = await response.json();

      setGame(data);

    } catch (err) {
      console.error(err);

      setError(
        "Could not connect to the backend. Make sure Spring Boot is running on port 8080."
      );
    } finally {
      setLoading(false);
    }
  };


  // --------------------------------------------------
  // START GAME
  // --------------------------------------------------

  const startGame = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await fetch(
        `${API_URL}/games/startgame/${game.id}`,
        {
          method: "POST",
        }
      );

      if (!response.ok) {
        throw new Error("Could not start game.");
      }

      const data = await response.json();

      setGame(data);

    } catch (err) {
      console.error(err);

      setError("Could not start the game.");

    } finally {
      setLoading(false);
    }
  };


  // --------------------------------------------------
  // CHOOSE SKILL
  // --------------------------------------------------

  const chooseSkill = async (skill) => {

    /*
     * IMPORTANT:
     *
     * currentTurn tells us who plays the card.
     *
     * It does NOT tell us who chooses the skill.
     *
     * Skill chooser rotates every round:
     *
     * Round 1 -> Player 1
     * Round 2 -> Player 2
     * Round 3 -> Player 1
     * Round 4 -> Player 2
     *
     */

    const skillChooserIndex =
      (game.currentRound - 1) % game.numberOfPlayers;

    const skillChooser =
      game.players[skillChooserIndex];

    try {

      const response = await fetch(
        `${API_URL}/games/choose-skill/${game.id}/${skillChooser.id}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(skill),
        }
      );

      if (!response.ok) {
        throw new Error("Could not choose skill.");
      }

      const data = await response.json();

      setGame(data);

    } catch (err) {

      console.error(err);

      setError("Could not choose skill.");

    }
  };


  // --------------------------------------------------
  // PLAY CARD
  // --------------------------------------------------

  const playCard = async (cardId) => {

    // currentTurn DOES determine who plays the card
    const currentPlayer =
      game.players[game.currentTurn - 1];

    try {

      const response = await fetch(
        `${API_URL}/games/play/${game.id}/${currentPlayer.id}/${cardId}`,
        {
          method: "POST",
        }
      );

      if (!response.ok) {
        throw new Error("Could not play card.");
      }

      const data = await response.json();

      setGame(data);

    } catch (err) {

      console.error(err);

      setError("Could not play card.");

    }
  };


  // --------------------------------------------------
  // CARD VALUE
  // --------------------------------------------------

  const getSkillValue = (card) => {

    if (!card || !card.cricketer) {
      return 0;
    }

    switch (game.currentSkill) {

      case "BATTING":
        return card.cricketer.batting;

      case "BOWLING":
        return card.cricketer.bowling;

      case "FIELDING":
        return card.cricketer.fielding;

      case "KEEPING":
        return card.cricketer.keeping;

      default:
        return 0;
    }
  };


  // --------------------------------------------------
  // CREATE GAME SCREEN
  // --------------------------------------------------

  if (!game) {

    return (

      <div className="app">

        <div className="game-container">

          <div className="logo">
            🏏
          </div>

          <h1>CRICKET CARD BATTLE</h1>

          <p className="subtitle">
            Build your team. Choose your skill. Defeat your opponent.
          </p>

          <div className="start-card">

            <h2>Create Game</h2>

            <input
              type="text"
              placeholder="Player 1 name"
              value={player1}
              onChange={(e) => setPlayer1(e.target.value)}
            />

            <input
              type="text"
              placeholder="Player 2 name"
              value={player2}
              onChange={(e) => setPlayer2(e.target.value)}
            />

            <button
              disabled={loading}
              onClick={createGame}
            >
              {loading ? "CREATING..." : "CREATE GAME"}
            </button>

            {error && (
              <p className="error">
                {error}
              </p>
            )}

          </div>

        </div>

      </div>

    );
  }


  // --------------------------------------------------
  // WAITING SCREEN
  // --------------------------------------------------

  if (game.status === "WAITING") {

    return (

      <div className="app">

        <div className="game-container">

          <div className="logo">
            🏏
          </div>

          <h1>GAME CREATED!</h1>

          <p className="subtitle">
            Game ID: <strong>{game.id}</strong>
          </p>

          <div className="start-card">

            <h2>Players</h2>

            {game.players.map((player) => (

              <div
                key={player.id}
                className="player-box"
              >

                <span>
                  {player.playerName}
                </span>

                <span>
                  Score: {player.score}
                </span>

              </div>

            ))}

            <button
              onClick={startGame}
              disabled={loading}
            >
              {loading ? "STARTING..." : "START GAME"}
            </button>

            {error && (
              <p className="error">
                {error}
              </p>
            )}

          </div>

        </div>

      </div>

    );
  }


  // --------------------------------------------------
  // GAME OVER
  // --------------------------------------------------

  if (game.status === "GAME_OVER") {

    return (

      <div className="app">

        <div className="game-container">

          <div className="logo">
            🏆
          </div>

          <h1>GAME OVER</h1>

          <p className="winner">

            {game.winner === "TIE"
              ? "IT'S A TIE!"
              : `WINNER: ${game.winner}`}

          </p>

          <div className="start-card">

            <h2>Final Scores</h2>

            {game.players.map((player) => (

              <div
                key={player.id}
                className="player-box"
              >

                <span>
                  {player.playerName}
                </span>

                <span>
                  Score: {player.score}
                </span>

              </div>

            ))}

          </div>

        </div>

      </div>

    );
  }


  // --------------------------------------------------
  // ACTIVE GAME
  // --------------------------------------------------

  const currentPlayer =
    game.players[game.currentTurn - 1];


  // --------------------------------------------------
  // FIND SKILL CHOOSER
  // --------------------------------------------------

  const skillChooserIndex =
    (game.currentRound - 1) % game.numberOfPlayers;

  const skillChooser =
    game.players[skillChooserIndex];


  return (

    <div className="app">

      <div className="game-container">

        <div className="logo">
          🏏
        </div>

        <h1>
          CRICKET CARD BATTLE
        </h1>

        <p className="subtitle">
          Round {game.currentRound} / 10
        </p>


        {/* SCOREBOARD */}

        <div className="scoreboard">

          {game.players.map((player) => (

            <div
              key={player.id}
              className={
                player.id === currentPlayer.id
                  ? "score-player active-player"
                  : "score-player"
              }
            >

              <strong>
                {player.playerName}
              </strong>

              <span>
                Score: {player.score}
              </span>

            </div>

          ))}

        </div>


        {/* CURRENT TURN */}

        <div className="turn-box">

          {game.currentSkill ? (

            <>

              <h2>
                {currentPlayer.playerName}'s Turn
              </h2>

              <p>
                Skill: <strong>{game.currentSkill}</strong>
              </p>

            </>

          ) : (

            <>

              <h2>
                {skillChooser.playerName}'s Turn
              </h2>

              <p>
                Choose the skill for this round
              </p>

            </>

          )}

        </div>


        {/* SKILL SELECTION */}

        {!game.currentSkill && (

          <div className="start-card">

            <h2>
              {skillChooser.playerName}, Choose Skill
            </h2>

            <div className="skill-buttons">

              <button
                onClick={() => chooseSkill("BATTING")}
              >
                🏏 BATTING
              </button>

              <button
                onClick={() => chooseSkill("BOWLING")}
              >
                🎯 BOWLING
              </button>

              <button
                onClick={() => chooseSkill("FIELDING")}
              >
                🧤 FIELDING
              </button>

              <button
                onClick={() => chooseSkill("KEEPING")}
              >
                🧤 KEEPING
              </button>

            </div>

          </div>

        )}


        {/* CARDS */}

        {game.currentSkill && (

          <div className="cards-section">

            <h2>
              {currentPlayer.playerName}'s Cards
            </h2>

            <div className="cards-grid">

              {currentPlayer.cards
                .filter((card) => !card.used)
                .map((card) => (

                  <div
                    key={card.id}
                    className="cricket-card"
                    onClick={() => playCard(card.id)}
                  >

                    <h3>
                      {card.cricketer.name}
                    </h3>

                    <div className="card-stat">
                      🏏 Batting:
                      <strong>
                        {card.cricketer.batting}
                      </strong>
                    </div>

                    <div className="card-stat">
                      🎯 Bowling:
                      <strong>
                        {card.cricketer.bowling}
                      </strong>
                    </div>

                    <div className="card-stat">
                      🧤 Fielding:
                      <strong>
                        {card.cricketer.fielding}
                      </strong>
                    </div>

                    <div className="card-stat">
                      🧤 Keeping:
                      <strong>
                        {card.cricketer.keeping}
                      </strong>
                    </div>

                    <div className="selected-stat">
                      {game.currentSkill}:

                      <strong>
                        {getSkillValue(card)}
                      </strong>

                    </div>

                    <button>
                      PLAY CARD
                    </button>

                  </div>

                ))}

            </div>

          </div>

        )}


        {error && (
          <p className="error">
            {error}
          </p>
        )}

      </div>

    </div>

  );
}

export default App;
