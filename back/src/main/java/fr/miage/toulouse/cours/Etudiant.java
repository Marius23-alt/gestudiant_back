package fr.miage.toulouse.cours;

/**
 * Classe qui créer des étudiants
 */

public class Etudiant {

    private String nom;
    private String prenom;
    private String numEtudiant;
    private String idMention;
    private String idParcours;
    private int semestreActuel;

    /**
     * Créer un étudiant
     * @param nom le nom de l'étudiant
     * @param prenom le prénom de l'étudiant
     * @param numEtudiant le numéro étudiant de l'étudiant
     */
    public Etudiant(String nom, String prenom, String numEtudiant, String idMention, String idParcours, int semestreActuel) {
        this.nom = nom;
        this.prenom = prenom;
        this.numEtudiant = numEtudiant;
        this.idMention = idMention;
        this.idParcours = idParcours;
        this.semestreActuel = semestreActuel;
    }

    /**
     * donne le nom de l'étudiant
     * @return le nom de l'étudiant
     */
    public String getNom() {
        return nom;
    }

    /**
     * donne le prenom de l'étudiant
     * @return le prenom de l'étudiant
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * donne le numéro étudiant de l'étudiant
     * @return le numéro étudiant de l'étudiant
     */
    public String getNumEtudiant() {
        return numEtudiant;
    }

    /**
     * Doonne le nom de la mention de l'étudiant
     * @return Le nom de la mention
     */
    public String getIdMention(){ return idMention; }

    /**
     * Donne le nom du parcours de l'étudiant
     * @return le nom du parcours de l'étudiant
     */
    public String getIdParcours(){return this.idParcours;}

    /**
     * Donne le numéro du semestre actuel
     * @return L'entier du semestre actuel
     */
    public int getSemestreActuel(){return this.semestreActuel;}

    /**
     * Pour modifier le nom de l'étudiant
     * @param nom le nom mis à jour de l'étudiant
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Pour modifier le prénom de l'étudiant
     * @param prenom le nom mis à jour de l'étudiant
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * Pour modifier le numéro étudiant
     * @param numEtudiant le numéro étudiant mis à jour de l'étudiant
     */
    public void setNumEtudiant(String numEtudiant) {
        this.numEtudiant = numEtudiant;
    }

    /**
     * Pour modifier le parcour
     * @param idParcours le nom du parcours mis à jours de l'étudiant
     */
    public void setIdParcours(String idParcours){this.idParcours = idParcours;}

    /**
     * Pour modifier la mention
     * @param idMention le nom de la mention mis à jours de l'étudiant
     */
    public void setIdMention(String idMention){this.idMention = idMention; }

    /**
     * Pour modifier le semestre actuel
     * @param semestreActuel l'entier du semestre mis à jours de l'étudiant
     */
    public void setSemestreActuel(int semestreActuel){this.semestreActuel = semestreActuel;}

}
