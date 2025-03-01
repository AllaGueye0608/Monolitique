package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.repository.EnseignementRepository;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.repository.ECRepository;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.service.ECService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model.UE;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.repository.UERepository;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.repository.MaquetteRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UEService {
    @Autowired
    private UERepository ueRepository;
    @Autowired
    private ECService ecService;
    @Autowired
    private ECRepository ecRepository;
    @Autowired
    private MaquetteRepository maquetteRepository;
    @Autowired
    private EnseignementRepository enseignementRepository;

    public UE create(UE ue){
        UE ueExisting = ueRepository.findByIntitule(ue.getIntitule());
        if(ueExisting != null){
            return null;
        }else{
            return ueRepository.save(ue);
        }
    }
    public UE update(UE ue) {
        UE ueExisting = ueRepository.findById(ue.getId()).orElse(null); // Utilisation de orElse pour éviter les exceptions
        if (ueExisting == null) {
            return null; // Entité non trouvée
        } else {
            return ueRepository.save(ue); // Mise à jour de l'UE
        }
    }

    public boolean delete(Long id) {
        UE ue = ueRepository.findById(id).orElse(null);
        if (ue == null) {
            return false; // L'UE n'existe pas
        }

        // Suppression des EC liés à l'UE
        List<EC> ecList = ue.getEcList();
        for (EC ec : ecList) {
            ecService.delete(ec); // Mise à jour des EC pour éviter la contrainte d'intégrité
        }

        // Suppression des relations avec les maquettes
        List<Maquette> maquettes = ue.getMaquettes();
        for (Maquette maquette : maquettes) {
            maquette.getUeList().remove(ue);
            maquetteRepository.save(maquette);
        }

        // Suppression de l'UE
        ueRepository.delete(ue);
        return true;
    }
    public List<UE> findAll(){
        return ueRepository.findAll();
    }

    public UE findById(Long id) {
        return ueRepository.findById(id).orElse(null); // Retourne null si non trouvé
    }

    public UE findByIntitule(String intitule){
        return ueRepository.findByIntitule(intitule);
    }

    public void addUEEC(UE ue, EC ec) {
        if (ec != null && ue != null) {
            ec.setUe(ue);
            if (!ue.getEcList().contains(ec)) {
                ue.getEcList().add(ec);
            }
            if (ecService.findByCode(ec.getCode()) != null) {
                ecService.update(ec);
            } else {
                ecService.create(ec);
            }
            update(ue);
        }
    }


    public UE findByCode(String code){
        return ueRepository.findByCode(code);
    }

    public void activer(Long id){
        UE ue = ueRepository.findById(id).get();
        if(ue != null){
            if(ue.isActive()){
                ue.setActive(false);
            }else{
                ue.setActive(true);
            }
            ueRepository.save(ue);
        }
    }

    public void archiver(Long id){
        UE ue = ueRepository.findById(id).get();
        if(ue != null){
            if(ue.isArchive()){
                ue.setArchive(false);
            }else{
                ue.setArchive(true);
            }
        }
    }

}
