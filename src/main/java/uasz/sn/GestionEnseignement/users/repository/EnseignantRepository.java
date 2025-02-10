package uasz.sn.GestionEnseignement.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.users.model.Enseignant;
@Repository
public interface EnseignantRepository extends JpaRepository<Enseignant,Long> {
    public Enseignant findByUsername(String username);
}
