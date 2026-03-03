package fr.miage.toulouse.cours;

import java.util.ArrayList;
import java.util.List;

public class Mention {
    private int id;
    private String nom;

    private List<Parcour> listParcours = new ArrayList<>();

    public Mention(int id, String nom) {
        this.nom = nom;
        this.id = id;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) { this.nom = nom;}

    public int getId(){return this.id;}
    public void setId(int id){this.id = id;}

    public void addParcour (Parcour parcour){
        this.listParcours.add(parcour);
    }
    public List<Parcour> getListParcours() {
        return listParcours;
    }
}
