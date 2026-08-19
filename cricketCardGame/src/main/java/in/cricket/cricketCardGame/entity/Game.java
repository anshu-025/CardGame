package in.cricket.cricketCardGame.entity;

import in.cricket.cricketCardGame.enums.Skill;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity

public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private int numberOfPlayers ;
    private int currentRound ;
    private int currentTurn;
    private String status;
    @Enumerated(EnumType.STRING)
    private Skill currentSkill;
    private String winner ;



    @OneToMany(mappedBy = "game" ,cascade = CascadeType.ALL) //cascade --> parent pe kiye hue operation child pe apply kro
    private List<GamePlayer> players = new ArrayList<>();


    public List<GamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<GamePlayer> players) {
        this.players = players;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Skill getCurrentSkill() {
        return currentSkill;
    }

    public void setCurrentSkill(Skill currentSkill) {
        this.currentSkill = currentSkill;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
