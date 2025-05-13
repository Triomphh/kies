package dev.triomph.kies.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.triomph.kies.pojo.Game;
import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.pojo.Question;
import java.util.List;
import java.util.Optional;

public interface QuestionDAO extends JpaRepository<Question, Long> {

    List<Question> findByGame(Game game);

    List<Question> findByGameAndAskingPlayer(Game game, Player askingPlayer);

    List<Question> findByGameAndTargetPlayer(Game game, Player targetPlayer);
    
    List<Question> findByGameAndGameRoundNumber(Game game, Integer gameRoundNumber);

    Optional<Question> findTopByGameAndAnswerNotOrderByTimestampDesc(Game game, dev.triomph.kies.pojo.Answer answer);
}