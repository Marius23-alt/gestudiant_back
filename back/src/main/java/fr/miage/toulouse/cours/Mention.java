package fr.miage.toulouse.cours;

import java.util.ArrayList;
import java.util.List;

public class Mention {

    private String nom;
    private List<Parcour> listParcours = new ArrayList<>();

    public Mention(String nom) {
        this.nom = nom;
    }

    public void addParcour (Parcour parcour){
        this.listParcours.add(parcour);
    }
    public String getNom() {
        return nom;
    }

    public List<Parcour> getListParcours() {
        return listParcours;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
}
