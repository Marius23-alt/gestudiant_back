package fr.miage.toulouse.cours;

/**
 * Classe représentant l'inscription entre un étudiant et une ue
 */
public class Inscription {

    private Etudiant etudiant;
    private Ue ue;
    private String annee;
    private String statut;

    /**
     * Construit une inscription
     * @param etudiant l'étudiant qu'on inscrit
     * @param ue l'ue à laquelle l'étudiant sera inscrit
     * @param annee l'année d'inscription
     * @param statut soit 'en_cours' soit 'valide'
     */
    public Inscription(Etudiant etudiant, Ue ue, String annee, String statut){
        this.etudiant = etudiant;
        this.ue = ue;
        this.annee = annee;
        this.statut = statut;
    }

    //Getters
    public Etudiant getEtudiant(){return this.etudiant;}
    public Ue getUe(){return this.ue;}
    public String getAnnee(){return this.annee;}
    public String getStatut(){return this.statut;}

    //Setters
    public void setEtudiant(Etudiant e){this.etudiant = e;}
    public void setUe(Ue u){this.ue = u;}
    public void setAnnee(String a){this.annee = a;}
    public void setStatut(String s){this.statut = s;}

}
