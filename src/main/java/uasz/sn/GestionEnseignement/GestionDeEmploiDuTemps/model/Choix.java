package uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model;

import jakarta.persistence.*;
import lombok.Data;
import uasz.sn.GestionEnseignement.users.model.Enseignant;

import java.time.LocalDate;

@Data
@Entity
public class Choix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // Type d'enseignement
    private LocalDate dateChoix; // Date du choix

    @ManyToOne
    @JoinColumn(name = "enseignant_id")
    private Enseignant enseignant;

    @ManyToOne
    @JoinColumn(name = "enseignement_id")
    private Enseignement enseignement;
    private boolean valide;
}
