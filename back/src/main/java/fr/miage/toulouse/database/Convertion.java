package fr.miage.toulouse.database;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Mention;
import fr.miage.toulouse.cours.Parcour;
import fr.miage.toulouse.cours.Ue;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Convertion {

    private Convertion () {}

    /**
     * Convertit une ligne de la BDD en un Objet Java complet (avec ses sous-objets)
     */
    public static Etudiant toEtudiant(ResultSet rs) throws SQLException {

        // On fabrique d'abord la Mention (la plus petite poupée russe)
        Mention mention = new Mention(
                rs.getInt("id_mention"),
                rs.getString("mention")
        );

        // On fabrique ensuite le Parcours (et on met la Mention dedans)
        Parcour parcour = new Parcour(
                rs.getInt("id_parcours"),
                rs.getString("parcour"),
                mention
        );

        // Enfin, on fabrique l'Étudiant (et on met le Parcours dedans)
        return new Etudiant(
                rs.getInt("num_etu"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getDate("date_naissance").toLocalDate(),
                parcour,
                rs.getInt("semestre"),
                rs.getInt("ects")
        );
    }

    // On laisse ces méthodes en bas au cas où, mais c'est surtout toEtudiant qui fera tout le travail
    public static Parcour toParcour(ResultSet rs) throws SQLException {
        return new Parcour(rs.getInt("id_parcours"), rs.getString("nom_parcours"), null);
    }

    public static Mention toMention(ResultSet rs) throws SQLException {
        return new Mention(rs.getInt("id_mention"), rs.getString("nom_mention"));
    }

    public static Ue toUe(ResultSet rs) throws SQLException {
        return new Ue(
                rs.getString("code_ue"),
                rs.getString("nom_ue"),
                rs.getInt("nb_credits"),
                1 // Ajout d'un semestre par défaut si un objet Ue en a un dans son constructeur
        );
    }
}