package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionFormation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.model.Classe;

import java.util.List;

@Entity
@Data @NoArgsConstructor
@AllArgsConstructor
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false)
    private String intitule;
    @OneToMany(mappedBy = "formation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Classe> classes;
    private boolean archive ;
}
