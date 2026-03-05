package fr.miage.toulouse.cours;

public class Ue {

    private String code;
    private String nom;
    private int nbCredit;
    private int semestre;
    private Parcour parcour;

    public Ue(String code, String nom, int nbCredit, int semestre, Parcour parcours) {
        this.code = code;
        this.nom = nom;
        this.nbCredit = nbCredit;
        this.semestre = semestre;
    }

    // --- Getters ---
    public String getCode() { return code; }
    public String getNom() { return nom; }
    public int getNbCredit() { return nbCredit; }
    public int getSemestre() { return semestre; }
    public Parcour getParcour() {return  this.parcour;}

    // --- Setters ---
    public void setCode(String code) { this.code = code; }
    public void setNom(String nom) { this.nom = nom; }
    public void setCredit(int nbCredit) { this.nbCredit = nbCredit; }
    public void setSemestre(int semestre) { this.semestre = semestre; }
    public void setParcour(Parcour parcour) { this.parcour = parcour; }

}