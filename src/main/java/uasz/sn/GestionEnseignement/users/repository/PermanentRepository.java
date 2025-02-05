package uasz.sn.GestionEnseignement.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.users.model.Permanent;
@Repository
public interface PermanentRepository extends JpaRepository<Permanent,Long> {
    public Permanent findByUsername(String username);
    public Permanent findByMatricule(String matricule);
}
