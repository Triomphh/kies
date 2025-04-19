package dev.triomph.kies.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.triomph.kies.DAO.RoundDAO;
import dev.triomph.kies.pojo.Game;
import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.pojo.Round;
import dev.triomph.kies.pojo.Status;

@Service
public class RoundService {

    private final RoundDAO roundDAO;

    public RoundService(RoundDAO roundDAO) {
        this.roundDAO = roundDAO;
    }

    public List<Round> getAllRounds() {
        return roundDAO.findAll();
    }

    public Optional<Round> getRoundById(Long id) {
        return roundDAO.findById(id);
    }

    public Round createRound(Integer roundNumber, Game game) {
        Round round = new Round(roundNumber, game);
        return roundDAO.save(round);
    }

    public Round createRoundWithStatus(Integer roundNumber, Game game, Status status) {
        Round round = new Round(roundNumber, game, status);
        return roundDAO.save(round);
    }

    public Round updateRound(Round round) {
        return roundDAO.save(round);
    }

    public Round setWinner(Long roundId, Player winner) {
        Optional<Round> roundOpt = roundDAO.findById(roundId);
        if (roundOpt.isPresent()) {
            Round round = roundOpt.get();
            round.setWinner(winner);
            round.setStatus(Status.COMPLETED);
            return roundDAO.save(round);
        }
        return null;
    }

    public Round updateStatus(Long roundId, Status status) {
        Optional<Round> roundOpt = roundDAO.findById(roundId);
        if (roundOpt.isPresent()) {
            Round round = roundOpt.get();
            round.setStatus(status);
            return roundDAO.save(round);
        }
        return null;
    }

    public void deleteRound(Long id) {
        roundDAO.deleteById(id);
    }
}