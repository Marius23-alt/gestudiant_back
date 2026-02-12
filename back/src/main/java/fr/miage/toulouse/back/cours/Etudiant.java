package fr.miage.toulouse.back.cours;

/**
 * Classe qui créer des étudiants
 */

public class Etudiant {

    private String nom;
    private String prenom;
    private String numEtudiant;
    private String id_mention;
    private String id_parcours;
    private int semestreActuel;

    /**
     * Créer un étudiant
     * @param nom le nom de l'étudiant
     * @param prenom le prénom de l'étudiant
     * @param numEtudiant le numéro étudiant de l'étudiant
     */
    public Etudiant(String nom, String prenom, String numEtudiant, String id_mention, String id_parcours, int semestreActuel) {
        this.nom = nom;
        this.prenom = prenom;
        this.numEtudiant = numEtudiant;
        this.id_mention = id_mention;
        this.id_parcours = id_parcours;
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
    public String getId_mention(){ return id_mention; }

    /**
     * Donne le nom du parcours de l'étudiant
     * @return le nom du parcours de l'étudiant
     */
    public String getId_parcours(){return this.id_parcours;}

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
     * @param id_parcours le nom du parcours mis à jours de l'étudiant
     */
    public void setId_parcours(String id_parcours){this.id_parcours = id_parcours;}

    /**
     * Pour modifier la mention
     * @param id_mention le nom de la mention mis à jours de l'étudiant
     */
    public void setId_mention(String id_mention){this.id_mention = id_mention; }

    /**
     * Pour modifier le semestre actuel
     * @param semestreActuel l'entier du semestre mis à jours de l'étudiant
     */
    public void setSemestreActuel(int semestreActuel){this.semestreActuel = semestreActuel;}

}
