package fr.miage.toulouse.cours;

public class Ue {

    private String code;
    private String nom;
    private int nbcredits;

    public Ue(String code, String nom, int nbcredits) {
        this.code = code;
        this.nom = nom;
        this.nbcredits = nbcredits;
    }

    public String getCode() {return this.code;}

    public String getNom() {return this.nom;}

    public int getNbcredits() {return this.nbcredits;}
}
