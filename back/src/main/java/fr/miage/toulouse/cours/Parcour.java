package fr.miage.toulouse.cours;

import java.util.ArrayList;
import java.util.List;

public class Parcour {

    private int id;
    private String nom;

    private Mention mention;

    private List<Etudiant> listEtudiants = new ArrayList<>();
    private List<Ue> listUes = new ArrayList<>();

    public Parcour(int id, String nom, Mention mention) {
        this.id = id;
        this.nom = nom;
        this.mention = mention;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getNom() { return nom; }
    public Mention getMention() { return mention; }
    public List<Etudiant> getListEtudiants() { return listEtudiants; }
    public List<Ue> getListUes() { return listUes; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setMention(Mention mention) { this.mention = mention; }

    public void addEtudiant(Etudiant e) {
        this.listEtudiants.add(e);
    }

    public void addUe(Ue ue) {
        this.listUes.add(ue);
    }
}