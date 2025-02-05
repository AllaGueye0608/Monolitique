package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionFormation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionFormation.model.Formation;

@Repository
public interface FormationRepository extends JpaRepository<Formation,Long> {
    public Formation findByIntitule(String intitule);
}
