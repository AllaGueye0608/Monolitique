package uasz.sn.GestionEnseignement.users.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Choix;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Seance;
import uasz.sn.GestionEnseignement.authentification.model.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public abstract class Enseignant extends User {
    private String specialite;
    private boolean archive;

    @OneToMany(mappedBy = "enseignant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seance> seances;

    // Relation ManyToMany avec l'entité Enseignement
    @ManyToMany
    @JoinTable(
            name = "choix",
            joinColumns = @JoinColumn(name = "enseignant_id"),
            inverseJoinColumns = @JoinColumn(name = "enseignement_id")
    )
    private List<Enseignement> enseignements = new ArrayList<>();
    private boolean responsable=false;

}