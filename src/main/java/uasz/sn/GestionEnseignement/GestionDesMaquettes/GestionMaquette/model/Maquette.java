package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.model.Classe;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model.UE;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "maquette",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"nom", "semestre"})}
)
public class Maquette {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private int semestre;

    @Transient
    private int credits;

    @Transient
    private int coeffUE;

    @Transient
    private int CMS;

    @Transient
    private int TDS;

    @Transient
    private int TPS;

    @Transient
    private int VHTS;

    @Transient
    private int TPES;

    @Transient
    private int coeffECs;

    @ManyToMany
    @JoinTable(
            name = "maquette_ue",
            joinColumns = @JoinColumn(name = "maquette_id"),
            inverseJoinColumns = @JoinColumn(name = "ue_id")
    )
    private List<UE> ueList = new ArrayList<>();

    private boolean archive;
    private boolean active;

    @ManyToOne
    private Classe classe;

    @OneToMany(mappedBy = "maquette", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enseignement> enseignements = new ArrayList<>();
}
