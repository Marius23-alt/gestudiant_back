package fr.miage.toulouse.cours;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une mention dans l'université
 */
public class Mention {

    private int id;
    private String nom;
    private List<Parcour> listParcours = new ArrayList<>();

    /**
     * Consytruit une mention
     * @param id l'identifiant de la mention
     * @param nom le nom de la mention
     */
    public Mention(int id, String nom) {
        this.nom = nom;
        this.id = id;
    }

    // Getters
    public int getId(){return this.id;}
    public String getNom() {
        return nom;
    }
    public List<Parcour> getListParcours() {
        return listParcours;
    }

    // Setters
    public void setId(int id){this.id = id;}
    public void setNom(String nom) { this.nom = nom;}
    public void addParcour (Parcour parcour){
        this.listParcours.add(parcour);
    }

}
