package fr.miage.toulouse.database;

import fr.miage.toulouse.cours.Etudiant;
import fr.miage.toulouse.cours.Inscription;
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
     * Récupère tous les étudiants de la base de données avec leurs infos complètes.
     * Les objets Parcours et Mention sont construits et imbriqués automatiquement.
     */
    public List<Etudiant> recupTousLesEtudiants() {

        // La requête qui ramène absolument TOUT ce dont Convertion.toEtudiant() a besoin
        String sql = "SELECT " +
                "E.num_etu, E.nom, E.prenom, E.date_naissance, " +
                "E.id_parcours, P.nom_parcours AS parcour, " +
                "P.id_mention, M.nom_mention AS mention, " +
                "E.semestre, " +
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
        String sql = "INSERT INTO Etudiant (num_etu, nom, prenom, date_naissance, semestre, id_parcours) VALUES (?, ?, ?, ?, ?, ?)";

        // On utilise this.conn qui est déjà ouverte par le constructeur !
        try (PreparedStatement pstmt = this.conn.prepareStatement(sql)) {

            pstmt.setInt(1, e.getNumEtu());
            pstmt.setString(2, e.getNom());
            pstmt.setString(3, e.getPrenom());
            // Conversion de la date (LocalDate vers java.sql.Date)
            pstmt.setDate(4, java.sql.Date.valueOf(e.getDateNaissance()));
            pstmt.setInt(5 ,e.getSemestreActuel());
            // On récupère l'ID du parcours via l'objet Parcours imbriqué
            pstmt.setInt(6, e.getParcour().getId());

            int lignesModifiees = pstmt.executeUpdate();
            return lignesModifiees > 0;

        } catch (SQLException ex) {
            log.log(Level.WARNING, ex, () -> "Erreur SQL lors de l'ajout de l'étudiant : " + ex.getMessage());
            return false;
        }
    }

    public List<Ue> recupToutesLesUe() {
        String sql = "SELECT UE.code_ue, UE.nom_ue, UE.nb_credits, " +
                "Structure_Parcours.semestrePrevu, " +
                "Parcours.id_parcours, Parcours.nom_parcours, " +
                "Mention.id_mention, Mention.nom_mention, " +
                "Prerequis.code_ue_requise " +
                "FROM UE " +
                "LEFT JOIN Structure_Parcours ON UE.code_ue = Structure_Parcours.code_ue " +
                "LEFT JOIN Parcours ON Structure_Parcours.id_parcours = Parcours.id_parcours " +
                "LEFT JOIN Mention ON Parcours.id_mention = Mention.id_mention " +
                "LEFT JOIN Prerequis ON UE.code_ue = Prerequis.code_ue";

        List<Ue> listeUe = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // Ta classe de conversion crée l'UE normalement
                Ue nouvelleUe = Convertion.toUe(rs);

                String codePrecedent = rs.getString("code_ue_requise");
                nouvelleUe.setCodeUePrecedente(codePrecedent);

                listeUe.add(nouvelleUe);
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
     * @return true si l'inscription a réussi et false sinon
     */
    public boolean ajouterInscitption(int numEtu, String code, String anneeUniv) {

        String sql = "Insert INTO Inscription (num_etu, code_ue, annee_univ, statut_validation) VALUES (?, ?, ?, ?)";

        try(PreparedStatement st = this.conn.prepareStatement(sql)){

            st.setInt(1,numEtu);
            st.setString(2,code);
            st.setString(3,anneeUniv);
            st.setString(4, "en_cours");

            int lignesModifiees = st.executeUpdate();
            return lignesModifiees == 1;

        }catch (SQLException e){
            log.log(Level.WARNING, e, () -> "Erreur SQL lors de l'inscription de l'étudiant à l'UE : " + e.getMessage());
            return false;
        }

    }

    /**
     * Permet de modifier le statut d'une UE pour un étudiant (passer en 'valide' ou 'echoue').
     * @param numEtu le numéro d'étudiant
     * @param code le code de l'Ue
     * @param anneeUniv l'année universitaire de l'inscription
     * @param nouveauStatut le nouveau statut ('valide' ou 'echoue')
     * @return true si la modification a réussi, false sinon
     */
    public boolean modifierStatutInscription(int numEtu, String code, String anneeUniv, String nouveauStatut) {

        String sql = "UPDATE Inscription SET statut_validation = ? WHERE num_etu = ? AND code_ue = ? AND annee_univ = ?";

        try (PreparedStatement st = this.conn.prepareStatement(sql)) {

            st.setString(1, nouveauStatut);
            st.setInt(2, numEtu);
            st.setString(3, code);
            st.setString(4, anneeUniv);

            int lignesModifiees = st.executeUpdate();
            return lignesModifiees == 1;

        } catch (SQLException e) {
            log.log(Level.WARNING, e, () -> "Erreur SQL lors de la modification du statut de l'UE : " + e.getMessage());
            return false;
        }
    }


    public void lierInscriptionsEnMemoire(List<Etudiant> listeEtudiants, List<Ue> listeUes) {
        String sql = "SELECT num_etu, code_ue, annee_univ, statut_validation FROM Inscription";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            int compteur = 0;
            while (rs.next()) {
                int numEtu = rs.getInt("num_etu");
                String codeUe = rs.getString("code_ue");
                String annee = rs.getString("annee_univ");
                String statut = rs.getString("statut_validation");

                Etudiant etudiantTrouve = listeEtudiants.stream()
                        .filter(e -> e.getNumEtu() == numEtu)
                        .findFirst().orElse(null);

                Ue ueTrouvee = listeUes.stream()
                        .filter(u -> u.getCode().equals(codeUe))
                        .findFirst().orElse(null);

                if (etudiantTrouve != null && ueTrouvee != null) {
                    Inscription inscr = new Inscription(etudiantTrouve, ueTrouvee, annee, statut);
                    etudiantTrouve.ajouterInscription(inscr);
                    ueTrouvee.ajouterInscription(inscr);
                    compteur++;
                }
            }
            String msg = "Request : " + compteur + " inscriptions tissées avec succès !";
            log.info(msg);

        } catch (SQLException e) {
            log.log(Level.WARNING, "Erreur lors du tissage des inscriptions", e);
        }
    }

    /**
     * Récupère la configuration temporelle courante de l'université.
     * @return Un tableau de String contenant : [0] = annee_univ, [1] = "true" ou "false" (pour le semestre impair)
     */
    public String[] recupConfigurationGlobale() {
        String[] config = new String[2];

        String sql = "SELECT annee_univ, est_impair FROM Historique_Semestre WHERE est_courant = TRUE LIMIT 1";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                config[0] = rs.getString("annee_univ");
                config[1] = String.valueOf(rs.getBoolean("est_impair"));
            }
        } catch (SQLException e) {
            log.log(Level.WARNING, "Erreur lors de la récupération de l'année courante", e);
            config[0] = "2024-2025";
            config[1] = "true";
        }
        return config;
    }

    /**
     * Met à jour les informations personnelles et le parcours d'un étudiant.
     * @param numEtu l'identifiant (le numéro d'étudiant)
     * @param nouveauNom le nouveau nom
     * @param nouveauPrenom le nouveau prénom
     * @param idNouveauParcours l'identifiant du nouveau parcours
     * @return true si la modification a réussi, false sinon.
     */
    public boolean updateEtudiant(int numEtu, String nouveauNom, String nouveauPrenom, Integer idNouveauParcours) {

        String sql = "UPDATE Etudiant SET nom = ?, prenom = ?, id_parcours = ? WHERE num_etu = ?";

        try (PreparedStatement st = this.conn.prepareStatement(sql)) {

            st.setString(1, nouveauNom);
            st.setString(2, nouveauPrenom);
            st.setInt(3, idNouveauParcours);
            st.setInt(4, numEtu);

            int lignesModifiees = st.executeUpdate();
            return lignesModifiees == 1;

        } catch (SQLException e) {
            log.log(Level.WARNING, e, () -> "Erreur SQL lors de la modification de l'étudiant " + numEtu + " : " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime un étudiant de la base de données en supprimant
     * également toutes les lignes de la table inscritption où il apparait.
     * @param etudiant L'étudiant à supprimer.
     * @return True si l'opération s'est bien passé false sinon.
     */
    public boolean supprimerEtudiant(Etudiant etudiant) {

        String sqlInscription = "DELETE FROM Inscription WHERE num_etu = ?";
        String sqlEtudiant = "DELETE FROM Etudiant WHERE num_etu = ?";

        try (
                PreparedStatement st1 = this.conn.prepareStatement(sqlInscription);
                PreparedStatement st2 = this.conn.prepareStatement(sqlEtudiant)
        ) {
            // suppression des inscriptions
            st1.setInt(1, etudiant.getNumEtu());
            st1.executeUpdate();

            // 2ème requête : suppression de l'étudiant
            st2.setInt(1, etudiant.getNumEtu());
            int lignesModifiees = st2.executeUpdate();

            return lignesModifiees == 1;

        } catch (SQLException e) {
            log.log(Level.WARNING, "Erreur lors de la suppression de l'étudiant", e);
            return false;
        }
    }
}
