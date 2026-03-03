package fr.miage.toulouse.cours;

import java.time.LocalDate;

public class Etudiant {

    private int numEtu;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;

    private int nbEcts;
    private int semestreActuel;

    private Parcour parcour;

    public Etudiant(int numEtu, String nom, String prenom, LocalDate dateNaissance, Parcour parcour, int semestreActuel, int nbEcts) {
        this.numEtu = numEtu;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.parcour = parcour;
        this.semestreActuel = semestreActuel;
        this.nbEcts = nbEcts;
    }

    // --- Getters ---
    public int getNumEtu() { return numEtu; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public int getNbEcts() { return nbEcts; }
    public int getSemestreActuel() { return semestreActuel; }
    public Parcour getParcour() { return parcour; } // Permettra de faire getParcour().getMention().getNom() !

    // --- Setters ---
    public void setNumEtu(int numEtu) { this.numEtu = numEtu; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setNbEcts(int nbEcts) { this.nbEcts = nbEcts; }
    public void setSemestreActuel(int semestreActuel) { this.semestreActuel = semestreActuel; }
    public void setParcour(Parcour parcour) { this.parcour = parcour; }
}