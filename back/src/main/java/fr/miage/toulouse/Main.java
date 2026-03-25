package fr.miage.toulouse;


import fr.miage.toulouse.database.Connexion;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Classe de test pour les requêtes vers la base de données et les conversions
 */
public class Main {

    private static final Logger log =  Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        try {
            Connexion.getConnexion();

            log.info("connexion ok !");

        }catch (SQLException e) {
            log.info("erreur connexion !");
        }
    }
}
