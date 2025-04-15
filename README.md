```
./mvnw spring-boot:run
```

LES POJOs SONT EXCLUS DU BUILD DANS CE COMMIT (pour ne pas empêcher le build) `pom.xml`


# Stack
- Svelte (frontend)
- Spring Boot avec JPA (backend)
- PostgreSQL (db)
- Traefik (reverse proxy)
- pgAdmin (gui db)

docker et docker-compose (je m'en charge)

Typescript pour le frontend ?

Traefik (reverse proxy):
- `/api/...` -> backend
- `/` -> frontend
- `/pgadmin` -> bah pgadmin


# Dépendances
Elles sont pas encore toutes ajoutées pour éviter des erreurs, mais on les ajoutera au fur et à mesure
Check `pom.xml/dependencies`
- Spring Web
- Spring DevTools
- Spring Data JPA
- PostgreSQL
- Spring Security (pour l'auth)
- Websocket
et Spring Test... mais tester c'est douter donc pas Spring Test



# Ressources utiles
- [Spring Initializr](https://start.spring.io/)
- [spring-boot-docker](https://spring.io/guides/gs/spring-boot-docker)
- [SpringBoot PostgreSQL Docker Guide](https://www.baeldung.com/spring-boot-postgresql-docker)