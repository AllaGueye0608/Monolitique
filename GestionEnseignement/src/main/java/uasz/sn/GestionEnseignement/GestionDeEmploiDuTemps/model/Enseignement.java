package uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model;

import jakarta.persistence.*;
import lombok.Data;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model.UE;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"maquette_id", "ue_id", "ec_id", "type"}))
public class Enseignement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @ManyToOne
    private UE ue;

    @ManyToOne
    private EC ec;

    @OneToOne(mappedBy = "enseignement", cascade = CascadeType.ALL, orphanRemoval = true)
    private Seance seance;

    @ManyToOne
    @JoinColumn(name = "maquette_id", nullable = false)
    private Maquette maquette;

    @OneToOne(mappedBy = "enseignement", cascade = CascadeType.ALL, orphanRemoval = true)
    private Choix choix; // Lié à la table de jointure Choix
}