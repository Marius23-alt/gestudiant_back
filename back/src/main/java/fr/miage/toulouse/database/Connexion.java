package fr.miage.toulouse.database;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Classe pour établir une connexion entre la base de données et l'application, avec récupération
 * des accès dans un fichier .env à placer dans le dossier back
 */
public class Connexion {

    private static final Dotenv dotenv = Dotenv
            .configure()
            .directory("../gestudiant_back/back/")
            .ignoreIfMissing()
            .load();
    private static final String URL = getVar("DB_URL");
    private static final String USER = getVar("DB_USER");
    private static final String PASSWORD = getVar("DB_PASSWORD");
    private static final Logger log = Logger.getLogger(Connexion.class.getName());
    private static Connection instanceUnique;

    /**
     *
     */
    private Connexion() {
    }

    private static String getVar(String key) {
        String v = dotenv.get(key);
        if (v != null && !v.isBlank()) return v;
        throw new IllegalStateException("Variable manquante : " + key);
    }

    /**
     * Permet de se connecter à la base de données (Singleton : qu'une seule connection)
     */
    public static Connection getConnexion() throws SQLException {
        if (instanceUnique == null || instanceUnique.isClosed()) {
            log.info("Création d'une NOUVELLE connexion à la base de données...");
            instanceUnique = DriverManager.getConnection(URL, USER, PASSWORD);
        } else {
            log.info("Réutilisation de la connexion EXISTANTE !");
        }
        return instanceUnique;
    }
}