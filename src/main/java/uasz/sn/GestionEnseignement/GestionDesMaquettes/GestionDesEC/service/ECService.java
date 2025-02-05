package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service.EnseignementService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.repository.ECRepository;

import java.util.List;

@Service
@Transactional
public class ECService {
    @Autowired
    private ECRepository ecRepository;
    @Autowired
    private EnseignementService enseignementService;

    public EC create(EC ec){
        EC ecExisting = ecRepository.findByIntitule(ec.getIntitule());
        if(ecExisting != null){
            return null;
        }else {
            return ecRepository.save(ec);
        }
    }

    public EC update(EC ec){
        EC ecExisting = ecRepository.findByCode(ec.getCode());
        if(ecExisting == null){
            return null;
        }else{
            return ecRepository.save(ec);
        }
    }

    public void delete(EC ec){
        if(ec.getEnseignements() != null){
            // Supprimer les Enseignements associés (si nécessaire)
            for (Enseignement enseignement : ec.getEnseignements()) {
                enseignementService.delete(enseignement);
            }
        }
        ecRepository.delete(ec);
    }

    public List<EC> findAll(){
        return ecRepository.findAll();
    }
    public EC findByIntitule(String intitule){
        return ecRepository.findByIntitule(intitule);
    }

    public  EC findById(Long id){
        return ecRepository.findById(id).get();
    }
    public EC findByCode(String code){
        return ecRepository.findByCode(code);
    }

    public void activer(Long id){
        EC ec = ecRepository.findById(id).get();
        if(ec!= null){
            if(ec.isActive()){
                ec.setActive(false);
            }else{
                ec.setActive(true);
            }
            ecRepository.save(ec);
        }
    }

    public void archiver(Long id){
        EC ec = ecRepository.findById(id).get();
        if(ec != null){
            if(ec.isArchive()){
                ec.setArchive(false);
            }else{
                ec.setArchive(true);
            }
        }
    }
}
