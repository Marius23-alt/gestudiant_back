package fr.miage.toulouse.cours;
import java.util.List;

public class Ue {

    private String code;
    private String nom;
    private int nbCredit;
    private int semestre;
    private Parcour parcour;
    private List<Inscription> inscription;

    public Ue(String code, String nom, int nbCredit, int semestre, Parcour parcours, List<Inscription> inscription) {
        this.code = code;
        this.nom = nom;
        this.nbCredit = nbCredit;
        this.semestre = semestre;
        this.parcour = parcours;
        this.inscription = inscription;
    }

    // --- Getters ---
    public String getCode() { return code; }
    public String getNom() { return nom; }
    public int getNbCredit() { return nbCredit; }
    public int getSemestre() { return semestre; }
    public Parcour getParcour() {return  this.parcour;}
    public List<Inscription> getInscription(){return this.inscription;}

    // --- Setters ---
    public void setCode(String code) { this.code = code; }
    public void setNom(String nom) { this.nom = nom; }
    public void setCredit(int nbCredit) { this.nbCredit = nbCredit; }
    public void setSemestre(int semestre) { this.semestre = semestre; }
    public void setParcour(Parcour parcour) { this.parcour = parcour; }
    public void ajouterInscription(Inscription i){this.inscription.add(i);}

}