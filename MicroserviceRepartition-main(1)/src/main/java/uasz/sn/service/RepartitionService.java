package uasz.sn.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.model.Repartition;
import uasz.sn.model.Enseignant;
import uasz.sn.model.Enseignement;
import uasz.sn.repository.RepartitionRepository;

import java.util.List;

@Service
public class RepartitionService {
    @Autowired
    private RepartitionRepository repartitionRepository;

    public Repartition findByEnseignantAndEnseignementAndType(Enseignant enseignant, Enseignement enseignement, String type){
        return repartitionRepository.findByEnseignantAndEnseignementAndType(enseignant,enseignement,type);
    }

    public List<Repartition> findByEnseignementAndType(Enseignement enseignement, String type){
        return repartitionRepository.findByEnseignementAndType(enseignement,type);
    }


    public Repartition findByEnseignement(Enseignement enseignement){
        return repartitionRepository.findByEnseignement(enseignement);
    }

    public  boolean estChoisi(Enseignement enseignement){
        if(repartitionRepository.findByEnseignement(enseignement) != null){
            return true;
        }else{
            return false;
        }
    }

    public Repartition valider(Long id){
        Repartition repartition = repartitionRepository.findById(id).get();
        if(repartition != null){
            if(repartition.isValide()){
                repartition.setValide(false);
            }else{
                repartition.setValide(true);
            }
            repartitionRepository.save(repartition);
        }
        return repartition;
    }

    public Repartition create(Repartition repartition){
        return repartitionRepository.save(repartition);
    }

    public Repartition update(Repartition repartition){
        Repartition repartition1 = repartitionRepository.findById(repartition.getId()).get();
        if(repartition != null){
            return repartitionRepository.save(repartition);
        }
        return null;
    }

    public void delete (Repartition repartition){
        repartitionRepository.delete(repartition);
    }

    public List<Repartition> findByEnseignant(Enseignant enseignant){
        return repartitionRepository.findByEnseignant(enseignant);
    }

    public Repartition findById(Long id){
        return repartitionRepository.findById(id).get();
    }

    public List<Repartition> findAll(){
        return  repartitionRepository.findAll();
    }


}
