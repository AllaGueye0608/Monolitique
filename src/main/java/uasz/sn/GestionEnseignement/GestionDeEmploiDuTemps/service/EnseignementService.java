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
        // Vérification si un enseignement identique existe
        Optional<Enseignement> enseignementExist = enseignementRepository
                .findByEcIdAndType(enseignement.getEc().getId(), enseignement.getType());

        if (enseignementExist.isPresent()) {
            // L'enseignement existe déjà
            return null; // ou vous pouvez renvoyer l'enseignement existant si besoin
        }

        // Si l'enseignement n'existe pas, on peut l'enregistrer
        return enseignementRepository.save(enseignement);
    }
    public Enseignement findByEcAndType(Long id,String type){
        return enseignementRepository.findByEcIdAndType(id,type).get();
    }

    public boolean exists(Maquette maquette, UE ue, EC ec, String type) {
        // Utilisation d'un repository pour vérifier si un enseignement existe avec les mêmes paramètres
        Enseignement enseignement = enseignementRepository.findByMaquetteAndUeAndEcAndType(maquette,ue, ec, type);
        return enseignement != null;
    }

    public  List<Enseignement> findByMaquetteAndUe(Maquette maquette, UE ue){
        return enseignementRepository.findByMaquetteAndUe(maquette,ue);
    }
    public List<Enseignement> findByUe(UE ue) {
        return enseignementRepository.findByUe(ue);
    }
    public Enseignement findByMaquetteAndEcAndType(Maquette maquette,EC ec , String type){
        return enseignementRepository.findByMaquetteAndEcAndType(maquette,ec,type);
    }
    public void delete(Enseignement enseignement) {
        enseignementRepository.delete(enseignement);
    }
}