package fr.miage.toulouse.database;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Parcour;
import fr.miage.toulouse.cours.Mention;



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
     * @return une liste d'étudiants
     */
    public List<Etudiant> recupEtudiant() {

        String sql = "SELECT distinct E.num_etu, E.nom, E.prenom, E.date_naissance, E.id_parcours, P.id_mention, I.semestre FROM Etudiant E INNER JOIN Parcours P ON E.id_parcours = P.id_parcours INNER JOIN Inscription I ON I.num_etu = E.num_etu WHERE I.statut_validation = 'en_cours'";

        List<Etudiant> listeEtudiants = new ArrayList<>();

        // Récupération des étudiants depuis la base de données
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




    public List<Parcour> recupParcoursParMention(Mention mention) {

        String nomMention = mention.getNom();

        // On utilise une jointure pour lier parcours et mention, et on filtre (?)
        String sql = "SELECT p.nom_parcours FROM Parcours p " +
                "JOIN Mention m ON p.id_mention = m.id_mention " +
                "WHERE m.nom_mention = ?";

        // On utilise un PreparedStatement quand on a des variables (?) pour la sécurité
        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, nomMention); // On remplace le '?' par le nom de la mention

            //Ajout des parcours à la liste des mentions dans la mention
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    mention.addParcour(Convertion.toParcour(rs));
                }
            }
        } catch (SQLException e) {
            log.log(Level.WARNING, "Erreur récupération parcours par mention", e);
        }
        return mention.getListParcours();
    }


    public List<Mention> recupMentions() {

        List<Mention> mentions = new ArrayList<>();
        String sql = "SELECT nom_mention FROM Mention";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                mentions.add(Convertion.toMention(rs));
            }
        } catch (SQLException e) {
            log.log(Level.WARNING, ERROR, e);
        }
        return mentions;
    }

    /**
     * Permet d'ajouter un étudiant à la base de donnée en donnant ses informations
     * @param etudiant l'étudiant à ajouter en base de données
     */

    // 1. On change 'void' en 'boolean'
    // On ajoute 'semestre' dans les paramètres

    public boolean ajouterEtudiant(Etudiant etudiant) {

        String sql = "INSERT INTO Etudiant (num_etu, nom, prenom, date_naissance, id_parcours) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, etudiant.getNumEtudiant());
            st.setString(2, etudiant.getNom());
            st.setString(3, etudiant.getPrenom());
            st.setString(4, etudiant.getDateNaissance());
            st.setString(5, etudiant.getIdParcours());

            int lignesModifiees = st.executeUpdate();

            if (lignesModifiees > 0) {

                return ajouterInscription(etudiant.getNumEtudiant(), "BDD_SQL", "2023-2024", etudiant.getSemestreActuel()); // Renvoie true si les deux ont marché
            }
            return false;

        } catch (SQLException e) {
            log.log(Level.WARNING, "Erreur lors de l'insertion de l'étudiant", e);
            return false;
        }
    }

    public boolean ajouterInscription(String numEtudiant, String codeUe, String anneeUniv, int semestre) {
        // On force le statut à 'en_cours' par défaut
        String sql = "INSERT INTO Inscription (num_etu, code_ue, annee_univ, semestre, statut_validation) VALUES (?, ?, ?, ?, 'en_cours')";

        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, numEtudiant);
            st.setString(2, codeUe);
            st.setString(3, anneeUniv);
            st.setInt(4, semestre);

            int lignesModifiees = st.executeUpdate();
            return lignesModifiees > 0;

        } catch (SQLException e) {
            log.log(Level.WARNING, "Erreur lors de l'insertion de l'inscription", e);
            return false;
        }
    }

    public ResultSet nbEcts (){ //Requette juste mais revoir le corps de la méthode avant de l'utiliser
        String sql = "SELECT SUM(nb_credits), num_etu FROM UE Inner join Inscription i ON UE.code_ue = i.code_ue where i.statut_validation = 'valide' group by num_etu;";

        try (Statement st = conn.createStatement()){
            return st.executeQuery(sql);

        }catch (SQLException e){
            log.log(Level.WARNING, ERROR, e);
            return null;
        }
    }

    public ResultSet ueAutorises (){ //Requette juste mais revoir le corps de la méthode avant de l'utiliser
        String sql = "SELECT UE.code_ue FROM UE WHERE code_ue NOT IN " +
                            "(SELECT Prerequis.code_ue FROM Prerequis WHERE Prerequis.code_ue_requise NOT IN " +
                                "(SELECT Inscription.code_ue FROM Inscription WHERE Inscription.num_etu = ?AND Inscription.statut_validation = 'valide'" +
                "             )" +
                "              ) " +
                "       AND UE.code_ue NOT IN  " +
                "           (SELECT Inscription.code_ue FROM Inscription WHERE (Inscription.statut_validation = 'valide' OR Inscription.statut_validation = 'en_cours') AND Inscription.num_etu = ? );";

        // RAJOUTER VARIABLE À LA PLACE DES 2 ?
        // IL FAUT METTRE LE NUM ÉTUDIANT DE L'ÉTUDIANT EN QUESTION

        try (Statement st = conn.createStatement()){
            return st.executeQuery(sql);

        }catch (SQLException e){
            log.log(Level.WARNING, ERROR, e);
            return null;
        }
    }

    public ResultSet ueEnCours (){ //Requette juste mais revoir le corps de la méthode avant de l'utiliser
        String sql = "SELECT Inscription.code_ue FROM Inscription WHERE Inscription.statut_validation = 'en_cours' AND Inscription.num_etu = 101; -- Mettre variable";

        // Mettre num étudiant à la place du ?

        try (Statement st = conn.createStatement()){
            return st.executeQuery(sql);

        }catch (SQLException e){
            log.log(Level.WARNING, ERROR, e);
            return null;
        }
    }

    public ResultSet ueEchoue (){ //Requette juste mais revoir le corps de la méthode avant de l'utiliser
        String sql = "SELECT Inscription.code_ue FROM Inscription WHERE Inscription.statut_validation = 'echoue' AND Inscription.num_etu = ?;";

        // Mettre num étudiant à la place du ?

        try (Statement st = conn.createStatement()){
            return st.executeQuery(sql);

        }catch (SQLException e){
            log.log(Level.WARNING, ERROR, e);
            return null;
        }
    }
}
