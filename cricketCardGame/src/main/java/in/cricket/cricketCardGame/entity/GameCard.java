package in.cricket.cricketCardGame.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class GameCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JsonIgnore
    private Game game;

    @ManyToOne
    @JsonIgnore
    private GamePlayer player;

    @ManyToOne
    private Cricketer cricketer;

    private boolean used;

    private int playedRound;

    public int getPlayedRound() {
        return playedRound;
    }

    public void setPlayedRound(int playedRound) {
        this.playedRound = playedRound;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Game getGame() {
        return game;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public void setPlayer(GamePlayer player) {
        this.player = player;
    }

    public Cricketer getCricketer() {
        return cricketer;
    }

    public void setCricketer(Cricketer cricketer) {
        this.cricketer = cricketer;
    }
}
