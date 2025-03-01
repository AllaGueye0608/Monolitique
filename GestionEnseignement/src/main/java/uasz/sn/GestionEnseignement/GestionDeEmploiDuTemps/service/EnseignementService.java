package uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.repository.EnseignementRepository;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model.UE;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;

import java.util.List;
import java.util.Optional;

@Service
public class EnseignementService {

    @Autowired
    private EnseignementRepository enseignementRepository;

    public List<Enseignement> findAll() {
        return enseignementRepository.findAll();
    }

    public List<Enseignement> findByMaquette(Maquette maquette){
        return enseignementRepository.findByMaquette(maquette);
    }
    public Enseignement findById(Long id) {
        return enseignementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enseignement not found"));
    }
    public Enseignement save(Enseignement enseignement) {
        return enseignementRepository.save(enseignement);
    }
    public Enseignement findByEcAndType(Long id){
        return enseignementRepository.findByEcId(id).get();
    }

    public boolean exists(Maquette maquette,EC ec) {
        // Utilisation d'un repository pour vérifier si un enseignement existe avec les mêmes paramètres
        Enseignement enseignement = enseignementRepository.findByMaquetteAndEc(maquette, ec);
        return enseignement != null;
    }


    public Enseignement findByMaquetteAndEc(Maquette maquette,EC ec ){
        return enseignementRepository.findByMaquetteAndEc(maquette,ec);
    }
    public void delete(Enseignement enseignement) {
        enseignementRepository.delete(enseignement);
    }
}