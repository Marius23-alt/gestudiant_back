package fr.miage.toulouse.cours;

import java.time.LocalDate;
import java.util.List;

public class Etudiant {
    private int numEtu;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private List<Inscription> inscription;
    private int nbEcts;
    private int semestreActuel;

    private Parcour parcour;

    public Etudiant(int numEtu, String nom, String prenom, LocalDate dateNaissance, List<Inscription> inscription, Parcour parcour, int semestreActuel, int nbEcts) {
        this.numEtu = numEtu;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.inscription = inscription;
        this.parcour = parcour;
        this.semestreActuel = semestreActuel;
        this.nbEcts = nbEcts;
    }

    // --- Getters ---
    public int getNumEtu() { return numEtu; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public List<Inscription> getInscription(){return this.inscription;}
    public int getNbEcts() { return nbEcts; }
    public int getSemestreActuel() { return semestreActuel; }
    public Parcour getParcour() { return parcour; } // Permettra de faire getParcour().getMention().getNom() !

    // --- Setters ---
    public void setNumEtu(int numEtu) { this.numEtu = numEtu; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public void ajouterInscription(Inscription i){this.inscription.add(i); }
    public void setNbEcts(int nbEcts) { this.nbEcts = nbEcts; }
    public void setSemestreActuel(int semestreActuel) { this.semestreActuel = semestreActuel; }
    public void setParcour(Parcour parcour) { this.parcour = parcour; }
}