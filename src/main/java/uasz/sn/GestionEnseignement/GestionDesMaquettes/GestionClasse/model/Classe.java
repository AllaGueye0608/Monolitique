package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionFormation.model.Formation;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "classe",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"formation_id", "niveau"})
        }
)
public class Classe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int niveau;

    private int effectif;
    private int nombreGroupe;
    @OneToMany(mappedBy = "classe", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Maquette> maquettes;

    @ManyToOne
    private Formation formation;
    private boolean  archive;
}
