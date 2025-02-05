package uasz.sn.GestionEnseignement.users.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@PrimaryKeyJoinColumn(name ="id")
public class Vacataire extends Enseignant{
    private String niveau;
}
