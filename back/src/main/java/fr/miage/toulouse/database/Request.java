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
     * Récupère TOUS les étudiants de la base de données avec leurs infos complètes.
     * Les objets Parcours et Mention sont construits et imbriqués automatiquement.
     */
    public List<Etudiant> recupTousLesEtudiants() {

        // La requête qui ramène absolument TOUT ce dont Convertion.toEtudiant() a besoin
        String sql = "SELECT " +
                "E.num_etu, E.nom, E.prenom, E.date_naissance, " +
                "E.id_parcours, P.nom_parcours AS parcour, " +
                "P.id_mention, M.nom_mention AS mention, " +
                "COALESCE((SELECT MAX(sp.semestrePrevu) FROM Inscription I JOIN Structure_Parcours sp ON I.code_ue = sp.code_ue AND sp.id_parcours = E.id_parcours WHERE I.num_etu = E.num_etu), 1) AS semestre, " +
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
            log.log(Level.WARNING, ex, () -> "❌ Erreur SQL lors de l'ajout de l'étudiant : " + ex.getMessage());
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
     * @return true si l'inscription à réussi et false si non
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
            log.log(Level.WARNING, e, () -> "❌ Erreur SQL lors de l'inscription de l'étudiant à l'UE : " + e.getMessage());
            return false;
        }

    }

    /**
     * Permet de valider l'Ue d'un étudiant
     * @param numEtu le numéro d'étudiant
     * @param code le code de l'Ue
     * @param anneeUniv l'année universitaire au moment de l'inscription à l'Ue
     * @return true si la validation à réussi et false si non
     */
    public boolean valideUe(int numEtu, String code, String anneeUniv) {

        String sql = "UPDATE Inscription SET statut_validation = 'valide' WHERE num_etu = ? AND code_ue = ? AND annee_univ = ?";

        try (PreparedStatement st = this.conn.prepareStatement(sql)) {

            st.setInt(1,numEtu);
            st.setString(2,code);
            st.setString(3,anneeUniv);

            int lignesModifiees = st.executeUpdate();
            return lignesModifiees == 1;

        }catch (SQLException e){
            log.log(Level.WARNING, e, () -> "❌ Erreur SQL lors de la validation de l'UE : " + e.getMessage());
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
            System.out.println("Request : " + compteur + " inscriptions tissées avec succès !");

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

        // On cherche l'unique ligne qui est définie comme "courante"
        String sql = "SELECT annee_univ, semestre_impair FROM Annee_Universitaire WHERE est_courante = TRUE LIMIT 1";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                config[0] = rs.getString("annee_univ");
                config[1] = String.valueOf(rs.getBoolean("semestre_impair"));
            }
        } catch (SQLException e) {
            log.log(Level.WARNING, "Erreur lors de la récupération de l'année courante", e);
            // Valeurs de secours au cas où la table est vide
            config[0] = "2024-2025";
            config[1] = "true";
        }
        return config;
    }
}
