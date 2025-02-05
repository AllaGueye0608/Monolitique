package uasz.sn.GestionEnseignement.GestionDesSalles.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.GestionDesSalles.model.Salle;
import uasz.sn.GestionEnseignement.GestionDesSalles.repository.SalleRepository;

import java.util.List;

@Service
public class SalleService {
    @Autowired
    private SalleRepository salleRepository;

    public Salle create(Salle salle){
        return salleRepository.save(salle);
    }

    public Salle update(Salle salle){
        Salle salleExisting = salleRepository.findById(salle.getId()).get();
        if(salle == null){
            return null;
        }
        return salleRepository.save(salle);
    }

    public void delete(Salle salle){
        salleRepository.delete(salle);
    }

    public Salle findById(Long id){
        return  salleRepository.findById(id).get();
    }

    public List<Salle> findAll(){
        return salleRepository.findAll();
    }
}
