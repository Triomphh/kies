package dev.triomph.kies.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.triomph.kies.pojo.Game;
import dev.triomph.kies.pojo.Round;
import java.util.Optional;

public interface RoundDAO extends JpaRepository<Round, Long> {
    Optional<Round> findByGameAndRoundNumber(Game game, Integer roundNumber);
}