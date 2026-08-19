package in.cricket.cricketCardGame.repository;

import in.cricket.cricketCardGame.entity.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GamePlayerRepository extends JpaRepository<GamePlayer,Integer> {
    List<GamePlayer> findByGameId(int gameId);
}
