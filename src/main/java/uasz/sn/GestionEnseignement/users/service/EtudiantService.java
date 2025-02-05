package uasz.sn.GestionEnseignement.users.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.users.model.Etudiant;
import uasz.sn.GestionEnseignement.users.repository.EtudiantRepository;

import java.util.List;

@Service
@Transactional
public class EtudiantService {
    @Autowired
    private EtudiantRepository etudiantRepository;

    public Etudiant create(Etudiant etudiant){
        if(etudiantRepository.findByUsername(etudiant.getUsername()) != null){
            return null;
        }
        return etudiantRepository.save(etudiant);
    }
    public Etudiant update(Etudiant etudiant){
        if(etudiantRepository.findByUsername(etudiant.getUsername()) == null){
            return null;
        }
        return etudiantRepository.save(etudiant);
    }
    public void delete(Etudiant etudiant){
        if(etudiantRepository.findByUsername(etudiant.getUsername()) != null){
            etudiantRepository.delete(etudiant);
        }
    }
    public List<Etudiant> findAll(){
        return etudiantRepository.findAll();
    }
    public Etudiant findByUsername(String username){
        return etudiantRepository.findByUsername(username);
    }
    public Etudiant findByMatricule(String matricule){
        return etudiantRepository.findByMatricule(matricule);
    }
}
