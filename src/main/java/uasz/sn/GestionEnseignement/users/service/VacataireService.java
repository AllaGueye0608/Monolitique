package uasz.sn.GestionEnseignement.users.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.users.model.Vacataire;
import uasz.sn.GestionEnseignement.users.repository.VacataireRepository;

import java.util.List;
@Service
@Transactional
public class VacataireService {
    @Autowired
    private VacataireRepository vacataireRepository;

    public Vacataire create(Vacataire vacataire){
        if(vacataireRepository.findByUsername(vacataire.getUsername()) != null){
            return null;
        }
        return vacataireRepository.save(vacataire);
    }
    public Vacataire update(Vacataire vacataire){
        if(vacataireRepository.findByUsername(vacataire.getUsername()) == null){
            return null;
        }
        return vacataireRepository.save(vacataire);
    }
    public void delete(Vacataire vacataire){
        if(vacataireRepository.findByUsername(vacataire.getUsername()) != null){
            vacataireRepository.delete(vacataire);
        }
    }
    public List<Vacataire> findAll(){
        return vacataireRepository.findAll();
    }
    public Vacataire findByUsername(String username){
        return vacataireRepository.findByUsername(username);
    }
    public Vacataire findById(Long id){return vacataireRepository.findById(id).get();}
}
