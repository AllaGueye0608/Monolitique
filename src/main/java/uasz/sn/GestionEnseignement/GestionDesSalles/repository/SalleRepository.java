package uasz.sn.GestionEnseignement.GestionDesSalles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.GestionDesSalles.model.Salle;

@Repository
public interface SalleRepository extends JpaRepository<Salle,Long> {
}
