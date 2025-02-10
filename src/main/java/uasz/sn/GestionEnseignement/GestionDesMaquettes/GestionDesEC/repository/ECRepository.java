package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;

import java.util.List;

public interface ECRepository extends JpaRepository<EC,Long> {
    public List<EC> findByUeId(Long id);
    public EC findByIntitule(String intitule);
    public EC findByCode(String code);
}

