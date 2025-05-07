package dev.triomph.kies.security;

import dev.triomph.kies.DAO.AccountDAO;
import dev.triomph.kies.DAO.PlayerRepository;
import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.pojo.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    PlayerRepository playerRepository;
    
    @Autowired
    AccountDAO accountDAO;

    /**
     * Charge un utilisateur par son nom (qui peut être un pseudo ou un email)
     * Cette méthode essaie d'abord d'identifier l'utilisateur
     * par son ID, puis par email, et enfin par pseudo.
     * Quand plusieurs joueurs ont le même pseudo (rare après tous ces checks), renvoie le premier trouvé.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Vérifie d'abord si le nom est un ID de joueur
        try {
            Long playerId = Long.parseLong(username);
            return loadUserById(playerId);
        } catch (NumberFormatException e) {
            // Le nom n'est pas un ID de joueur, continue avec d'autres vérifications
        }
        
        // Essaie avec email
        Account account = accountDAO.findByEmail(username).orElse(null);
        if (account != null) {
            return UserDetailsImpl.build(account.getPlayer());
        }
        
        // Essaie avec pseudo
        Player player = playerRepository.findFirstByNickname(username).orElse(null);
        if (player != null) {
            return UserDetailsImpl.build(player);
        }
        
        throw new UsernameNotFoundException("No user found with ID, email, or nickname: " + username);
    }
    
    @Transactional
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with id: " + id));
        
        return UserDetailsImpl.build(player);
    }
    
    @Transactional
    public List<Player> findPlayersByNickname(String nickname) {
        return playerRepository.findAllByNickname(nickname);
    }
} 