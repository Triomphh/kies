package jpa;

import jpa.pojo.Player;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.*;

public class Main {
    public static void main(String[ ] args)
    {
        // Création de l'EntityManagerFactory
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("kies");
        // Récupération de l'EntityManager
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        // Création d'une entité
        Player player = new Player();
        player.setNickname("Martin");
        System.out.println(player.getNickname());
        // Début de la transaction
        entityManager.getTransaction().begin();
        // Persistance de l'entité dans la base de données
        entityManager.persist(player);
        // Validation de la transaction
        entityManager.getTransaction().commit();
        // Fermeture de l'EntityManager
        entityManager.close();
        // Fermeture de l'EntityManagerFactory
        entityManagerFactory.close();

    }
}
