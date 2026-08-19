package in.cricket.cricketCardGame.repository;
import in.cricket.cricketCardGame.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game,Integer>{

}
