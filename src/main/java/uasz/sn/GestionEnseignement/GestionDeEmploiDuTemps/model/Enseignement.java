package uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model;

import jakarta.persistence.*;
import lombok.Data;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model.UE;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;
import uasz.sn.GestionEnseignement.users.model.Enseignant;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"maquette_id", "ue_id", "ec_id", "type"}))
public class Enseignement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private EC ec;

    @OneToMany(mappedBy = "enseignement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seance> seances;

    @ManyToOne
    @JoinColumn(name = "maquette_id", nullable = false)
    private Maquette maquette;

    // Relation ManyToMany avec l'entité Enseignant
    @ManyToMany(mappedBy = "enseignements")
    private List<Enseignant> enseignants = new ArrayList<>();
}