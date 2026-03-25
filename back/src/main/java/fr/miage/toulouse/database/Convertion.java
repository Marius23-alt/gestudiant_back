package fr.miage.toulouse.database;

import fr.miage.toulouse.cours.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe pour convertir les données de la base de données en objet java
 */
public class Convertion {

    private static final String NOM_MENTION="nom_mention";
    private static final String NOM_PARCOURS="nom_parcours";
    private static final String ID_MENTION="id_mention";
    private static final String ID_PARCOURS="id_parcours";

    private Convertion () {}

    /**
     * Convertit une ligne de la BDD en un Objet Java complet (avec ses sous-objets)
     */
    public static Etudiant toEtudiant(ResultSet rs) throws SQLException {

        // On fabrique d'abord la Mention (la plus petite poupée russe)
        Mention mention = new Mention(
                rs.getInt(ID_MENTION),
                rs.getString("mention")
        );

        // On fabrique ensuite le Parcours (et on met la Mention dedans)
        Parcour parcour = new Parcour(
                rs.getInt(ID_PARCOURS),
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

    /**
     * Convertit une ligne de la table Parcours en un objet java parcour mais rattaché à aucun parcours
     * @param rs le résultat de la requête de récupération d'un parcours
     * @return l'objet parcour crée
     * @throws SQLException renvoie une exception SQL s'il y a eu une erreur lors
     * de la récupération du tuple
     */
    public static Parcour toParcour(ResultSet rs) throws SQLException {
        return new Parcour(rs.getInt(ID_PARCOURS), rs.getString(NOM_PARCOURS), null);
    }

    /**
     * Convertit une ligne de la table Mention en un objet java mention
     * @param rs le résultat de la requête de récupération de la mention
     * @return l'objet mention crée
     * @throws SQLException renvoie une exception SQL s'il y a eu une erreur lors
     * de la récupération du tuple
     */
    public static Mention toMention(ResultSet rs) throws SQLException {
        return new Mention(rs.getInt(ID_MENTION), rs.getString(NOM_MENTION));
    }

    /**
     * Convertit une ligne de la table Ue en un objet java parcour mais rattaché à aucun parcours ni mention
     * @param rs le résultat de la requête de récupération d'une ue
     * @return l'objet ue crée
     * @throws SQLException renvoie une exception SQL s'il y a eu une erreur lors
     * de la récupération du tuple
     */
    public static Ue toUe(ResultSet rs) throws SQLException {
        Mention mention = null;
        Parcour parcour = null;


        if (rs.getString("NOM_MENTION") != null) {
            mention = new Mention(
                    rs.getInt(ID_MENTION),
                    rs.getString("NOM_MENTION")
            );
        }

        if (rs.getString(NOM_PARCOURS) != null) {
            parcour = new Parcour(
                    rs.getInt(ID_PARCOURS),
                    rs.getString(NOM_PARCOURS),
                    mention
            );
        }

        return new Ue(
                rs.getString("code_ue"),
                rs.getString("nom_ue"),
                rs.getInt("nb_credits"),
                rs.getInt("semestrePrevu"),
                parcour
        );
    }
}