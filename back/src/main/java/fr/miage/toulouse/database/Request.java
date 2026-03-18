package fr.miage.toulouse.database;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Ue;

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
     * Récupère TOUS les étudiants de la base de données avec leurs infos complètes.
     * Les objets Parcours et Mention sont construits et imbriqués automatiquement.
     */
    public List<Etudiant> recupTousLesEtudiants() {

        // La requête qui ramène absolument TOUT ce dont Convertion.toEtudiant() a besoin
        String sql = "SELECT " +
                "E.num_etu, E.nom, E.prenom, E.date_naissance, " +
                "E.id_parcours, P.nom_parcours AS parcour, " +
                "P.id_mention, M.nom_mention AS mention, " +
                "COALESCE((SELECT MAX(semestre) FROM Inscription WHERE num_etu = E.num_etu), 1) AS semestre, " +
                "COALESCE((SELECT SUM(nb_credits) FROM UE INNER JOIN Inscription I ON UE.code_ue = I.code_ue WHERE I.num_etu = E.num_etu AND I.statut_validation = 'valide'), 0) AS ects " +
                "FROM Etudiant E " +
                "JOIN Parcours P ON E.id_parcours = P.id_parcours " +
                "JOIN Mention M ON P.id_mention = M.id_mention";

        List<Etudiant> listeEtudiants = new ArrayList<>();

        try (PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                // Ici, la magie opère : Convertion fabrique la Mention, la met dans le Parcours, qu'il met dans l'Etudiant !
                listeEtudiants.add(Convertion.toEtudiant(rs));
            }
        } catch (SQLException e) {
            log.log(Level.WARNING, "Erreur lors du chargement des étudiants", e);
        }
        return listeEtudiants;
    }

    /**
     * Ajoute un nouvel étudiant dans la base de données.
     * @param e L'objet Etudiant contenant les informations saisies.
     * @return true si l'insertion a réussi, false sinon.
     */
    public boolean ajouterEtudiant(Etudiant e) {
        // On insère uniquement les infos de la table Etudiant
        String sql = "INSERT INTO Etudiant (num_etu, nom, prenom, date_naissance, id_parcours) VALUES (?, ?, ?, ?, ?)";

        // On utilise this.conn qui est déjà ouverte par le constructeur !
        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {

            pstmt.setInt(1, e.getNumEtu());
            pstmt.setString(2, e.getNom());
            pstmt.setString(3, e.getPrenom());
            // Conversion de la date (LocalDate vers java.sql.Date)
            pstmt.setDate(4, java.sql.Date.valueOf(e.getDateNaissance()));
            // On récupère l'ID du parcours via l'objet Parcours imbriqué
            pstmt.setInt(5, e.getParcour().getId());

            int lignesModifiees = pstmt.executeUpdate();
            return lignesModifiees > 0;

        } catch (SQLException ex) {
            log.log(Level.WARNING, "❌ Erreur SQL lors de l'ajout de l'étudiant : " + ex.getMessage(), ex);
            return false;
        }
    }
    public List<Ue> recupToutesLesUe() {
        String sql = "SELECT UE.code_ue, UE.nom_ue, UE.nb_credits, " +
                "Structure_Parcours.semestrePrevu, " +
                "Parcours.id_parcours, Parcours.nom_parcours, " +
                "Mention.id_mention, Mention.nom_mention " +
                "FROM UE " +
                "LEFT JOIN Structure_Parcours ON UE.code_ue = Structure_Parcours.code_ue " +
                "LEFT JOIN Parcours ON Structure_Parcours.id_parcours = Parcours.id_parcours " +
                "LEFT JOIN Mention ON Parcours.id_mention = Mention.id_mention";

        List<Ue> listeUe = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                listeUe.add(Convertion.toUe(rs));
            }
        } catch (SQLException e) {
            log.log(Level.WARNING, "Erreur lors du chargement des UE", e);
        }
        return listeUe;
    }

    /**
     * Inscription d'un étudiant à des Ue
     * @param numEtu le numéro d'étudiant
     * @param code le code de l'Ue
     * @param anneeUniv l'année universitaire au moment de l'inscription à l'Ue
     * @param semestre le semestre de l'Ue
     * @return true si l'inscription à réussi et false si non
     */
    public boolean ajouterInscitption(int numEtu, String code, String anneeUniv, int semestre) {

        String sql = "Insert INTO Inscription (num_etu, code_ue, annee_univ, semestre, statut_validation) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement st = this.conn.prepareStatement(sql)){

            st.setInt(1,numEtu);
            st.setString(2,code);
            st.setString(3,anneeUniv);
            st.setInt(4,semestre);
            st.setString(5, "en_cours");

            int lignesModifiees = st.executeUpdate();
            return lignesModifiees == 1;

        }catch (SQLException e){
            log.log(Level.WARNING, e, () -> "❌ Erreur SQL lors de l'ajout de l'étudiant : " + e.getMessage());
            return false;
        }

    }

    /**
     * Permet de valider l'Ue d'un étudiant
     * @param numEtu le numéro d'étudiant
     * @param code le code de l'Ue
     * @param anneeUniv l'année universitaire au moment de l'inscription à l'Ue
     * @param semestre le semestre de l'Ue
     * @return true si la validation à réussi et false si non
     */
    public boolean valideUe(int numEtu, String code, String anneeUniv, int semestre) {

        String sql = "UPDATE Inscription SET statut_validation = 'valide' WHERE num_etu = ? AND code_ue = ? AND annee_univ = ? AND semestre = ?";

        try (PreparedStatement st = this.conn.prepareStatement(sql)) {

            st.setInt(1,numEtu);
            st.setString(2,code);
            st.setString(3,anneeUniv);
            st.setInt(4,semestre);

            int lignesModifiees = st.executeUpdate();
            return lignesModifiees == 1;

        }catch (SQLException e){
            log.log(Level.WARNING, e, () -> "❌ Erreur SQL lors de l'ajout de l'étudiant : " + e.getMessage());
            return false;
        }
    }

//    /**
//     * Permet de récuperer la liste des étudiants
//     * @return une liste d'étudiants
//     */
//    public List<Etudiant> recupEtudiant() {
//
//        String sql = "SELECT distinct E.num_etu, E.nom, E.prenom, E.date_naissance, E.id_parcours, P.id_mention, I.semestre FROM Etudiant E INNER JOIN Parcours P ON E.id_parcours = P.id_parcours INNER JOIN Inscription I ON I.num_etu = E.num_etu WHERE I.statut_validation = 'en_cours'";
//
//        List<Etudiant> listeEtudiants = new ArrayList<>();
//
//        // Récupération des étudiants depuis la base de données
//        try (PreparedStatement st = conn.prepareStatement(sql);
//            ResultSet rs = st.executeQuery()) {
//
//            while (rs.next()) {
//                listeEtudiants.add(Convertion.toEtudiant(rs));
//            }
//            return listeEtudiants;
//
//        }catch (SQLException e){
//            log.log(Level.WARNING, ERROR, e);
//            return listeEtudiants;
//        }
//    }
//
//
//
//
//    public List<Parcour> recupParcoursParMention(Mention mention) {
//
//        String nomMention = mention.getNom();
//
//        // On utilise une jointure pour lier parcours et mention, et on filtre (?)
//        String sql = "SELECT p.nom_parcours FROM Parcours p " +
//                "JOIN Mention m ON p.id_mention = m.id_mention " +
//                "WHERE m.nom_mention = ?";
//
//        // On utilise un PreparedStatement quand on a des variables (?) pour la sécurité
//        try (PreparedStatement pst = conn.prepareStatement(sql)) {
//
//            pst.setString(1, nomMention); // On remplace le '?' par le nom de la mention
//
//            //Ajout des parcours à la liste des mentions dans la mention
//            try (ResultSet rs = pst.executeQuery()) {
//                while (rs.next()) {
//                    mention.addParcour(Convertion.toParcour(rs));
//                }
//            }
//        } catch (SQLException e) {
//            log.log(Level.WARNING, "Erreur récupération parcours par mention", e);
//        }
//        return mention.getListParcours();
//    }
//
//
//    public List<Mention> recupMentions() {
//
//        List<Mention> mentions = new ArrayList<>();
//        String sql = "SELECT nom_mention FROM Mention";
//
//        try (Statement st = conn.createStatement();
//             ResultSet rs = st.executeQuery(sql)) {
//
//            while (rs.next()) {
//                mentions.add(Convertion.toMention(rs));
//            }
//        } catch (SQLException e) {
//            log.log(Level.WARNING, ERROR, e);
//        }
//        return mentions;
//    }
//
//    /**
//     * Permet d'ajouter un étudiant à la base de donnée en donnant ses informations
//     * @param etudiant l'étudiant à ajouter en base de données
//     */
//
//    // 1. On change 'void' en 'boolean'
//    // On ajoute 'semestre' dans les paramètres
//
//    public boolean ajouterEtudiant(Etudiant etudiant) {
//
//        String sql = "INSERT INTO Etudiant (num_etu, nom, prenom, date_naissance, id_parcours) VALUES (?, ?, ?, ?, ?)";
//
//        try (PreparedStatement st = conn.prepareStatement(sql)) {
//            st.setString(1, etudiant.getNumEtudiant());
//            st.setString(2, etudiant.getNom());
//            st.setString(3, etudiant.getPrenom());
//            st.setDate(4, java.sql.Date.valueOf(etudiant.getDateNaissance()));
//            st.setString(5, etudiant.getIdParcours());
//
//            int lignesModifiees = st.executeUpdate();
//
//            if (lignesModifiees > 0) {
//
//                return ajouterInscription(etudiant.getNumEtudiant(), "BDD_SQL", "2023-2024", etudiant.getSemestreActuel()); // Renvoie true si les deux ont marché
//            }
//            return false;
//
//        } catch (SQLException e) {
//            log.log(Level.WARNING, "Erreur lors de l'insertion de l'étudiant", e);
//            return false;
//        }
//    }
//
//    public boolean ajouterInscription(String numEtudiant, String codeUe, String anneeUniv, int semestre) {
//        // On force le statut à 'en_cours' par défaut
//        String sql = "INSERT INTO Inscription (num_etu, code_ue, annee_univ, semestre, statut_validation) VALUES (?, ?, ?, ?, 'en_cours')";
//
//        try (PreparedStatement st = conn.prepareStatement(sql)) {
//            st.setString(1, numEtudiant);
//            st.setString(2, codeUe);
//            st.setString(3, anneeUniv);
//            st.setInt(4, semestre);
//
//            int lignesModifiees = st.executeUpdate();
//            return lignesModifiees > 0;
//
//        } catch (SQLException e) {
//            log.log(Level.WARNING, "Erreur lors de l'insertion de l'inscription", e);
//            return false;
//        }
//    }
//
//    public ResultSet nbEcts (){ //Requette juste mais revoir le corps de la méthode avant de l'utiliser
//        String sql = "SELECT SUM(nb_credits), num_etu FROM UE Inner join Inscription i ON UE.code_ue = i.code_ue where i.statut_validation = 'valide' group by num_etu;";
//
//        try (Statement st = conn.createStatement()){
//            return st.executeQuery(sql);
//
//        }catch (SQLException e){
//            log.log(Level.WARNING, ERROR, e);
//            return null;
//        }
//    }
//
//    public List<Ue> ueAutorises (Etudiant etudiant){ //Requette juste mais revoir le corps de la méthode avant de l'utiliser
//
//        List<Ue> listUe = new ArrayList<>();
//
//        String sql = "SELECT UE.code_ue ,UE.nom_ue, UE.nb_credits FROM UE WHERE code_ue NOT IN " +
//                            "(SELECT Prerequis.code_ue FROM Prerequis WHERE Prerequis.code_ue_requise NOT IN " +
//                                "(SELECT Inscription.code_ue FROM Inscription WHERE Inscription.num_etu = ?AND Inscription.statut_validation = 'valide'" +
//                "             )" +
//                "              ) " +
//                "       AND UE.code_ue NOT IN  " +
//                "           (SELECT Inscription.code_ue FROM Inscription WHERE (Inscription.statut_validation = 'valide' OR Inscription.statut_validation = 'en_cours') AND Inscription.num_etu = ? );";
//
//        // RAJOUTER VARIABLE À LA PLACE DES 2 ?
//        // IL FAUT METTRE LE NUM ÉTUDIANT DE L'ÉTUDIANT EN QUESTION
//
//        try (PreparedStatement st = conn.prepareStatement(sql)){
//            st.setString(1, etudiant.getNumEtudiant());
//            st.setString(2, etudiant.getNumEtudiant());
//
//            ResultSet rs = st.executeQuery();
//
//            while (rs.next()) {
//                listUe.add(Convertion.toUe(rs));
//            }
//
//            return listUe;
//
//        }catch (SQLException e){
//            log.log(Level.WARNING, ERROR, e);
//            return null;
//        }
//    }
//
//    public ResultSet ueEnCours (){ //Requette juste mais revoir le corps de la méthode avant de l'utiliser
//        String sql = "SELECT Inscription.code_ue FROM Inscription WHERE Inscription.statut_validation = 'en_cours' AND Inscription.num_etu = 101; -- Mettre variable";
//
//        // Mettre num étudiant à la place du ?
//
//        try (Statement st = conn.createStatement()){
//            return st.executeQuery(sql);
//
//        }catch (SQLException e){
//            log.log(Level.WARNING, ERROR, e);
//            return null;
//        }
//    }
//
//    public ResultSet ueEchoue (){ //Requette juste mais revoir le corps de la méthode avant de l'utiliser
//        String sql = "SELECT Inscription.code_ue FROM Inscription WHERE Inscription.statut_validation = 'echoue' AND Inscription.num_etu = ?;";
//
//        // Mettre num étudiant à la place du ?
//
//        try (Statement st = conn.createStatement()){
//            return st.executeQuery(sql);
//
//        }catch (SQLException e){
//            log.log(Level.WARNING, ERROR, e);
//            return null;
//        }
//    }
}
