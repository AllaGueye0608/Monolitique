package uasz.sn.GestionEnseignement.users.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.users.model.Enseignant;
import uasz.sn.GestionEnseignement.users.repository.EnseignantRepository;

import java.util.List;

@Service
@Transactional
public class EnseignantService {
    @Autowired
    private EnseignantRepository enseignantRepository;
    public Enseignant create(Enseignant enseignant){
        if(enseignantRepository.findByUsername(enseignant.getUsername()) != null){
            return null;
        }
        return enseignantRepository.save(enseignant);
    }
    public Enseignant update(Enseignant enseignant){
        if(enseignantRepository.findByUsername(enseignant.getUsername()) == null){
            return null;
        }
        return enseignantRepository.save(enseignant);
    }
    public  void delete(Enseignant enseignant){
        if(enseignantRepository.findByUsername(enseignant.getUsername()) != null){
            enseignantRepository.delete(enseignant);
        }
    }
    public Enseignant findById(Long id){
        return enseignantRepository.findById(id).get();
    }
    public Enseignant findByUsername(String username){
        return enseignantRepository.findByUsername(username);
    }
    public List<Enseignant> findAll(){
        return enseignantRepository.findAll();
    }
    public void activer(Long id){
        Enseignant enseignant = enseignantRepository.findById(id).get();
        if(enseignant != null){
            if(enseignant.isActive()){
                enseignant.setActive(false);
            }else{
                enseignant.setActive(true);
            }
            enseignantRepository.save(enseignant);
        }
    }
    public void archiver(Long id){
        Enseignant enseignant = enseignantRepository.findById(id).get();
        if(enseignant != null){
            if(enseignant.isArchive()){
                enseignant.setArchive(false);
            }else{
                enseignant.setArchive(true);
            }
        }
    }
}
