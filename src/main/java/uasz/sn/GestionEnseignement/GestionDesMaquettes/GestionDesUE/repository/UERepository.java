package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model.UE;

@Repository
public interface UERepository extends JpaRepository<UE,Long> {
    public UE findByIntitule(String intitule);
    public UE findByCode(String code);
}
