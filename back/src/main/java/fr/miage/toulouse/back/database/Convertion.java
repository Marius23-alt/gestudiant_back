package fr.miage.toulouse.back.database;

import fr.miage.toulouse.maven.cours.Etudiant;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Convertion {


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
