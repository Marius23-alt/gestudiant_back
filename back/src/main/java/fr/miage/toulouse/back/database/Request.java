package fr.miage.toulouse.back.database;

import fr.miage.toulouse.maven.cours.Etudiant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
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

        String sql = "SELECT distinct E.num_etu, E.nom, E.prenom, E.id_parcours, P.id_mention, I.semestre FROM etudiant E INNER JOIN parcours P ON E.id_parcours = P.id_parcours INNER JOIN inscription I ON I.num_etu = E.num_etu WHERE I.statut_validation = 'en_cours'";

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

    /**
     * Permet de récupérer les mention et parcours
     * @return une liste de p
     */
    public ResultSet recupParcourMention() {

        String sql = "SELECT distinct P.nom_parcours, M.nom_mention FROM mention M, parcours P WHERE P.id_mention = M.id_mention";

        try (Statement st = conn.createStatement()){
            return st.executeQuery(sql);

        }catch (SQLException e){
            log.log(Level.WARNING, ERROR, e);
            return null;
        }
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
