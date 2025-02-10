package uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model.UE;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnseignementRepository extends JpaRepository<Enseignement,Long> {
    public Optional<Enseignement> findByEcId(Long ecId);
    List<Enseignement> findByMaquette(Maquette maquette);
    Enseignement findByMaquetteAndEc(Maquette maquette,EC ec);
}
