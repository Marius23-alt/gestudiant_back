package fr.miage.toulouse;

import fr.miage.toulouse.cours.Ue;
import fr.miage.toulouse.database.Connexion;
import fr.miage.toulouse.database.Request;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;


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
