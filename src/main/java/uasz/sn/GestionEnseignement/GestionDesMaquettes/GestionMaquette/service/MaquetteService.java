package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model.UE;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.service.UEService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionFormation.model.Formation;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.repository.MaquetteRepository;
import uasz.sn.GestionEnseignement.users.model.Enseignant;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MaquetteService {
    @Autowired
    private MaquetteRepository maquetteRepository;
    @Autowired
    private UEService ueService;

    public Maquette create(Maquette maquette){
        Maquette existingMaquette = maquetteRepository.findByNomAndSemestre(maquette.getNom(),maquette.getSemestre());
        if (existingMaquette != null){
            return null;
        }else{
            return maquetteRepository.save(maquette);
        }
    }

    public Maquette update(Maquette maquette){
        Maquette existingMaquette = maquetteRepository.findById(maquette.getId()).get();
        if(existingMaquette == null){
            return null;
        }else{
            return maquetteRepository.save(maquette);
        }
    }

    public void delete(Maquette maquette){
        Maquette existingMaquette = maquetteRepository.findById(maquette.getId()).get();
        if(existingMaquette != null){
            maquetteRepository.delete(maquette);
        }
    }

    public List<Maquette> findAll(){
        return maquetteRepository.findAll();
    }

    public  Maquette findById(Long id){
        return maquetteRepository.findById(id).get();
    }

    public Maquette findByNom(String nom,int semestre){
        return maquetteRepository.findByNomAndSemestre(nom,semestre);
    }
    public void addUeToMaquette(Maquette maquette, List<UE> ues) {
        // Vérifier si la Maquette existe déjà
        Maquette existingMaquette = findByNom(maquette.getNom(), maquette.getSemestre());
        if (existingMaquette == null) {
            throw new RuntimeException("Maquette non trouvée avec le nom et semestre fournis.");
        }

        // Obtenir la liste existante des UE de la maquette
        List<UE> existingUeList = existingMaquette.getUeList();

        for (UE ue : ues) {
            // Ajouter l'UE à la Maquette si elle n'est pas déjà présente
            if (!existingUeList.contains(ue)) {
                existingUeList.add(ue);

                // Ajouter la Maquette à la liste des Maquettes de l'UE
                List<Maquette> ueMaquettes = ue.getMaquettes();
                if (!ueMaquettes.contains(existingMaquette)) {
                    ueMaquettes.add(existingMaquette);
                }
                ue.setMaquettes(ueMaquettes);

                // Mettre à jour l'UE
                ueService.update(ue);
            }
        }

        // Mettre à jour la liste des UE de la Maquette et sauvegarder
        existingMaquette.setUeList(existingUeList);
        maquetteRepository.save(existingMaquette);
    }

    public void archiver(Long id){
        Maquette maquette = maquetteRepository.findById(id).get();
        if(maquette != null){
            if(maquette.isArchive()){
                maquette.setArchive(false);
            }else{
                maquette.setArchive(true);
            }
        }
    }

    public void activer(Long id){
        Maquette maquette = maquetteRepository.findById(id).get();
        if(maquette != null){
            if(maquette.isActive()){
                maquette.setActive(false);
            }else{
                maquette.setActive(true);
            }
            maquetteRepository.save(maquette);
        }
    }

}
