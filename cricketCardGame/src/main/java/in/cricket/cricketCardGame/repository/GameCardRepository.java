package in.cricket.cricketCardGame.repository;
import in.cricket.cricketCardGame.entity.GameCard;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GameCardRepository extends JpaRepository<GameCard,Integer> {

}
