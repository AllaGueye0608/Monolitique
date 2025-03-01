package uasz.sn.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Enseignement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String semestre;
    private String niveau;
    private String formation;
    @ManyToMany(mappedBy = "enseignements",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Enseignant> enseignants = new ArrayList<>();
}
