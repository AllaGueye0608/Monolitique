package uasz.sn.GestionEnseignement.users.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uasz.sn.GestionEnseignement.authentification.model.User;
@Entity
@AllArgsConstructor @NoArgsConstructor @Data
@PrimaryKeyJoinColumn(name="id")
public class Etudiant extends User {
    @Column(unique = true)
    private String matricule;
}
