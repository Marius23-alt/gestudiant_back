package fr.miage.toulouse.database;

import fr.miage.toulouse.cours.Etudiant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe pour faire des requêtes à la base de données à partir de la classe Connexion
 */
public class Request {

    private Connection conn;
    private final Logger log =  Logger.getLogger(Request.class.getName());

    private static final String ERROR ="Erreur lors du chargement des données";

    /**
     * Construit une connexion à la base de données
     */
    public Request() {
        try {
            this.conn = Connexion.getConnexion();

        } catch (SQLException e) {
            log.log(Level.WARNING, ERROR, e);
        }
    }

    /**
     * Permet de récuperer la liste des étudiants
     * @return la liste des étudiants
     */
    public ObservableList<Etudiant> recupEtudiant() {

        String sql = "SELECT distinct E.num_etu, E.nom, E.prenom, E.id_parcours, P.id_mention, I.semestre FROM Etudiant E INNER JOIN Parcours P ON E.id_parcours = P.id_parcours INNER JOIN Inscription I ON I.num_etu = E.num_etu WHERE I.statut_validation = 'en_cours'";

        ObservableList<Etudiant> listeEtudiants = FXCollections.observableArrayList();

        try (PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                listeEtudiants.add(Convertion.toEtudiant(rs));
            }
            return listeEtudiants;

        }catch (SQLException e){
            log.log(Level.WARNING, ERROR, e);
            return listeEtudiants;
        }
    }

    public List<String> recupParcoursParMention(String nomMention) {
        List<String> parcours = new ArrayList<>();

        // On utilise une jointure pour lier parcours et mention, et on filtre (?)
        String sql = "SELECT p.nom_parcours FROM Parcours p " +
                "JOIN Mention m ON p.id_mention = m.id_mention " +
                "WHERE m.nom_mention = ?";

        // On utilise un PreparedStatement quand on a des variables (?) pour la sécurité
        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, nomMention); // On remplace le '?' par le nom de la mention

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    parcours.add(rs.getString("nom_parcours"));
                }
            }
        } catch (SQLException e) {
            log.log(Level.WARNING, "Erreur récupération parcours par mention", e);
        }
        return parcours;
    }

    public List<String> recupMentions() {
        List<String> mentions = new ArrayList<>();
        String sql = "SELECT nom_mention FROM Mention";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                mentions.add(rs.getString("nom_mention"));
            }
        } catch (SQLException e) {
            log.log(Level.WARNING, ERROR, e);
        }
        return mentions;
    }

    /**
     * Permet d'ajouter un étudiant à la base de donnée en donnant ses informations
     * @param numEtudiant son numéro étudiant
     * @param nom son nom
     * @param prenom son prenom
     * @param idParcours le parcours auquel il est rattaché
     */
    public void ajouterEtudiant(String numEtudiant, String nom, String prenom, String idParcours) {

        String sql = "INSERT INTO etudiant VALUES (?, ?, ?, ?)";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, numEtudiant);
            st.setString(2, nom);
            st.setString(3, prenom);
            st.setString(4, idParcours);

            st.executeUpdate();

        }catch (SQLException e){
            log.log(Level.WARNING, ERROR, e);
        }
    }
}
