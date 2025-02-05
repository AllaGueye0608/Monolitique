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
    public Optional<Enseignement> findByEcIdAndType(Long ecId, String type);
    Enseignement findByMaquetteAndUeAndEcAndType(Maquette maquette,UE ue, EC ec, String type);
    List<Enseignement> findByMaquetteAndUe(Maquette maquette, UE ue);
    List<Enseignement> findByUe(UE ue);
    Enseignement findByMaquetteAndEcAndType(Maquette maquette,EC ec,String type);
    List<Enseignement> findByMaquette(Maquette maquette);
}
