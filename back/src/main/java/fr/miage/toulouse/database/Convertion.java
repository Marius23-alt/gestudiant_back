package fr.miage.toulouse.database;

import fr.miage.toulouse.cours.Etudiant;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Convertion {


    private Convertion () {}


    public static Etudiant toEtudiant(ResultSet rs) throws SQLException{
        return new Etudiant(
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("num_etu"),
                rs.getString("id_mention"),
                rs.getString("id_parcours"),
                rs.getInt("semestre")
        );
    }
}
