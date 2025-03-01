package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;

import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(unique = true, nullable = false)
    private String intitule;

    private int credit;
    private int coefficient;

    private boolean active;
    private boolean archive;

    @OneToMany(mappedBy = "ue", fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    private List<EC> ecList = new ArrayList<>();

    @ManyToMany(mappedBy = "ueList")
    private List<Maquette> maquettes = new ArrayList<>();

}
