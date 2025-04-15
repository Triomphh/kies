

Il y a le version .html si vous n'avez pas de viewer markdown.


# Rapport : Données (Partie 1)
Driencourt Kévin, Conderine Florent


## Résumé
Pour mettre en place la partie persistance de l'application, nous avons défini les entités (POJO) et les avons mappé à une base de données PostgreSQL à l'aide de JPA.

## 1. Schéma de la base de données
<iframe width="560" height="315" src='https://dbdiagram.io/e/67ebec454f7afba184f3648e/67ebec6a4f7afba184f36a90'> </iframe>

## 2. Différentes entités (POJO)

- `Player.java`: Représente un joueur, avec pseudo unique, (connecté ou non) dans l'application. *(Nous avons décidé de laisser la possibilité à un joueur non connecté de participer et jouer aux parties de 'Qui est-ce?' avec un accès limités aux différentes fonctionnalités de l'app (ex: créer une grille custom...), comme un guest)*
- `Account.java`: Représente le compte d'un joueur enregistré en relation 1:1 (`@OneToOne`) avec `Player.java`
- `Game.java`: Représente une partie. Contient des informations sur le créateur, l'opposant, le nombre maximum de rounds, la limite de tours, le gagnant éventuel, et est liée à une `Grid`.
- `Round.java`: Représente un tour dans une partie.
- `Question.java`: Représente une question posée par un joueur (asker) pendant un Round. Contient le texte de la question, la réponse et un timestamp.
- `Grid.java`: Représente une grille de jeu, potentiellement créée par un joueur, et a une catégorie. 
- `Category.java`: Représente une catégorie de personnages ou d'éléments dans la grille, de même, potentiellement créée par un joueur.
- et des énumérations...

Les relations entre entités (`@OneToOne`, `@ManyToOne`) ont été définies avec des stratégies de chargement `FetchType.LAZY` pour optimiser les performances. Les clés primaires sont généralement auto-générées (`@GeneratedValue(strategy = GenerationType.IDENTITY)`) et les colonnes sont mappées avec `@Column`.


## 3. Configuration de la Persistance
La configuration de la persistance JPA est centralisée dans le fichier src/main/resources/META-INF/persistence.xml.

**Unité de Persistance :** Une unité de persistance nommée kies a été définie.

**Fournisseur JPA :** Hibernate (`org.hibernate.jpa.HibernatePersistenceProvider`) est utilisé comme implémentation de JPA.

**Classes Mappées :** Toutes les classes d'entités (`Account`, `Answer`, `Category`, `Game`, `Grid`, `Player`, `Question`, `Round`) sont explicitement listées dans le fichier persistence.xml pour être gérées par l'EntityManager.

**Propriétés Hibernate :**

- `hibernate.dialect`: Défini avec `org.hibernate.dialect.PostgreSQLDialect` pour adapter les requêtes SQL au SGBD PostgreSQL.
- `hibernate.show_sql`: pour afficher les requêtes SQL générées dans les logs (utile pour le débogage).
- `hibernate.format_sql`: pour formater joliment les requêtes SQL affichées.
- `jakarta.persistence.schema-generation.database.action`: pour mettre à jour le schéma de la base de données (créer ou modifier les tables et colonnes) au démarrage pour correspondre aux entités mappées. On changera cette valeur une fois le projet en production.

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_2.xsd"
             version="3.2">
    <persistence-unit name="kies" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <!-- Classes -->
        <class>jpa.pojo.Account</class>
        <class>jpa.pojo.Answer</class>
        <class>jpa.pojo.Category</class>
        <class>jpa.pojo.Game</class>
        <class>jpa.pojo.Grid</class>
        <class>jpa.pojo.Player</class>
        <class>jpa.pojo.Question</class>
        <class>jpa.pojo.Round</class>

        <properties>
            <!-- Connection Properties -->
            <property name="jakarta.persistence.jdbc.driver" value="org.postgresql.Driver"/>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/kiesdb"/>
            <property name="jakarta.persistence.jdbc.user" value="triomph"/>
            <property name="jakarta.persistence.jdbc.password" value="kies"/>

            <!-- Hibernate Properties -->
            <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
            <property name="jakarta.persistence.schema-generation.database.action" value="update"/>
        </properties>
    </persistence-unit>
</persistence>
```


## 4. Connexion à la base de données

Les informations de connexion à la base de données sont également définies dans `persistence.xml` :
- Driver : `org.postgresql.Driver`
- URL : `jdbc:postgresql://localhost:5432/kiesdb`
- Utilisateur, mot de passe...


## Conclusion

La première partie du projet a permis de mettre en place une couche de persistance en utilisant JPA. Les entités sont définies et mappées en essayant de respecter les contraintes de la base de données, et la configuration de la connexion à la base de données PostgreSQL est établie via le fichier persistence.xml.