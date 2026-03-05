package fr.miage.toulouse.cours;

public class Inscription {
    private Etudiant etudiant;
    private Ue ue;
    private String statut;
    private int semestre;

    public Inscription(Etudiant etudiant, Ue ue, String statut, int semestre){
        this.etudiant = etudiant;
        this.ue = ue;
        this.statut = statut;
        this.semestre = semestre;
    }

}
