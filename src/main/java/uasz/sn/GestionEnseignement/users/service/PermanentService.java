package uasz.sn.GestionEnseignement.users.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.users.model.Permanent;
import uasz.sn.GestionEnseignement.users.repository.PermanentRepository;

import java.util.List;

@Service
@Transactional
public class PermanentService {
    @Autowired
    private PermanentRepository permanentRepository;

    public Permanent create(Permanent permanent){
        if(permanentRepository.findByUsername(permanent.getUsername()) != null){
            return null;
        }
        return permanentRepository.save(permanent);
    }
    public Permanent update(Permanent permanent){
        if(permanentRepository.findByUsername(permanent.getUsername()) == null){
            return null;
        }
        return permanentRepository.save(permanent);
    }
    public void delete(Permanent permanent){
        if(permanentRepository.findByUsername(permanent.getUsername()) != null){
            permanentRepository.delete(permanent);
        }
    }
    public List<Permanent> findAll(){
        return permanentRepository.findAll();
    }
    public Permanent findByUsername(String username){
        return permanentRepository.findByUsername(username);
    }
    public Permanent findByMatricule(String matricule){
        return permanentRepository.findByMatricule(matricule);
    }
    public Permanent findById(Long id){return permanentRepository.findById(id).get();}
}
