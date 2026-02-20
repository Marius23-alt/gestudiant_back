package fr.miage.toulouse.database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Connexion {


    private static final Dotenv dotenv = Dotenv
            .configure()
            .directory("../gestudiant_back/back/")
            .ignoreIfMissing() // évite d'exploser si le .env n'est pas présent (ex: en prod)
            .load();

    private static final String URL = getVar("DB_URL");
    private static final String USER = getVar("DB_USER");
    private static final String PASSWORD = getVar("DB_PASSWORD");

    private static final Logger log =  Logger.getLogger(Connexion.class.getName());

    private Connexion() {
    }

    /**
     * Permet d'aller chercher les variables d'environnement
     * contenu dans le fichier .env se trouvant dans le workdirectory
     * @param key la clé enregistré dans le fichier .env
     * @return les valaurs associés axu clés
     */
    private static String getVar(String key) {


        // Récupére les valeurs associés aux clés
        String v = dotenv.get(key);
        if (v != null && !v.isBlank()) return v;

        // Renvoie une erreur car des variables d'environnement sont manquantes
        throw new IllegalStateException("Variable manquante : " + key + ". Définis-la dans .env ou dans l'environnement.");
    }

    /**
     * Permet de se connecter à la base de données
     * @return un objet de type Connection
     * @throws SQLException si la connection à échouée
     */
    public static Connection getConnexion() throws SQLException {

        log.info("connexion ok !");

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


}
