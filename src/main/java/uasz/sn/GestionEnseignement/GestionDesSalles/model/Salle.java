package uasz.sn.GestionEnseignement.GestionDesSalles.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Seance;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"batiment_id", "numero"})
)
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int numero;

    @ManyToOne
    @JoinColumn(name = "batiment_id", nullable = false)
    private Batiment batiment;
    @OneToMany(mappedBy = "salle")
    private List<Seance> seances;

}
