package dev.triomph.kies.DAO;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import dev.triomph.kies.pojo.Player;


public interface PlayerDAO extends JpaRepository<Player, Long> {
    Optional<Player> findByNickname(String nickname);
    List<Player> findAllByNickname(String nickname);
    boolean existsById(@NonNull Long id);
}