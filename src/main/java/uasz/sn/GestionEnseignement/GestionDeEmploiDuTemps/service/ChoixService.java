package uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Choix;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.repository.ChoixRepository;
import uasz.sn.GestionEnseignement.users.model.Enseignant;

import java.util.List;

@Transactional
@Service
public class ChoixService {
    @Autowired
    private ChoixRepository choixRepository;

    public Choix findByEnseignantAndEnseignementAndType(Enseignant enseignant, Enseignement enseignement,String type){
        return choixRepository.findByEnseignantAndEnseignementAndType(enseignant,enseignement,type);
    }

    public Choix findByEnseignementAndType(Enseignement enseignement,String type){
        return choixRepository.findByEnseignementAndType(enseignement,type);
    }

    public Choix findByEnseignement(Enseignement enseignement){
        return choixRepository.findByEnseignement(enseignement);
    }

    public  boolean estChoisi(Enseignement enseignement){
        if(choixRepository.findByEnseignement(enseignement) != null){
            return true;
        }else{
            return false;
        }
    }

    public Choix create(Choix choix){
        return choixRepository.save(choix);
    }

    public Choix update(Choix choix){
        Choix choix1 = choixRepository.findById(choix.getId()).get();
        if(choix != null){
            return choixRepository.save(choix);
        }
        return null;
    }

    public void delete (Choix choix){
        choixRepository.delete(choix);
    }

    public List<Choix> findByEnseignant(Enseignant enseignant){
        return choixRepository.findByEnseignant(enseignant);
    }

    public Choix findById(Long id){
        return choixRepository.findById(id).get();
    }

    public List<Choix> findAll(){
        return  choixRepository.findAll();
    }


}
