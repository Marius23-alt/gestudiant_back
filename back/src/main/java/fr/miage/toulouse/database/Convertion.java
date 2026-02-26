package fr.miage.toulouse.database;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Mention;
import fr.miage.toulouse.cours.Parcour;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Convertion {


    private Convertion () {}


    public static Etudiant toEtudiant(ResultSet rs) throws SQLException{
        return new Etudiant(
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("date_naissance"),
                rs.getString("num_etu"),
                rs.getString("id_mention"),
                rs.getString("id_parcours"),
                rs.getInt("semestre"),
                rs.getInt("ects")
        );
    }

    public static Parcour toParcour(ResultSet rs) throws SQLException{
        return new Parcour(rs.getString("nom_parcours"));
    }

    public static Mention toMention(ResultSet rs) throws SQLException{
        return new Mention(rs.getString("nom_mention"));
    }
}
