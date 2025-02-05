package uasz.sn.GestionEnseignement.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.users.model.Vacataire;

@Repository
public interface VacataireRepository extends JpaRepository<Vacataire,Long> {
    public Vacataire findByUsername(String username);
}
