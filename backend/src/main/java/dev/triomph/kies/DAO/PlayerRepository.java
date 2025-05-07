package dev.triomph.kies.DAO;

import dev.triomph.kies.pojo.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByNickname(String nickname);
    List<Player> findAllByNickname(String nickname);
    Boolean existsByNickname(String nickname);
    
    // Trouve le premier joueur avec ce pseudo (alternative à findByNickname)
    @Query("SELECT p FROM Player p WHERE p.nickname = ?1 ORDER BY p.playerId ASC")
    Optional<Player> findFirstByNickname(String nickname);
} 