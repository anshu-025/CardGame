package in.cricket.cricketCardGame.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Cricketer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private String name ;
    private int batting;
    private int bowling ;
    private int fielding ;
    private int keeping ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBatting() {
        return batting;
    }

    public void setBatting(int batting) {
        this.batting = batting;
    }

    public int getBowling() {
        return bowling;
    }

    public void setBowling(int bowling) {
        this.bowling = bowling;
    }

    public int getFielding() {
        return fielding;
    }

    public void setFielding(int fielding) {
        this.fielding = fielding;
    }

    public int getKeeping() {
        return keeping;
    }

    public void setKeeping(int keeping) {
        this.keeping = keeping;
    }
}
