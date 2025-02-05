package uasz.sn.GestionEnseignement.GestionDesSalles.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.GestionDesSalles.model.Batiment;
import uasz.sn.GestionEnseignement.GestionDesSalles.repository.BatimentRepository;

import java.util.List;

@Service
public class BatimentService {
    @Autowired
    private BatimentRepository batimentRepository;

    public Batiment create(Batiment batiment){
        return batimentRepository.save(batiment);
    }

    public Batiment update(Batiment batiment){
        Batiment batimentExisting = batimentRepository.findById(batiment.getId()).get();
        if(batimentExisting == null){
            return null;
        }
        return batimentRepository.save(batiment);
    }

    public  void delete(Batiment batiment){
        batimentRepository.delete(batiment);
    }

    public Batiment findById(Long id){
        return batimentRepository.findById(id).get();
    }

    public List<Batiment> getAll(){
        return batimentRepository.findAll();
    }
}
