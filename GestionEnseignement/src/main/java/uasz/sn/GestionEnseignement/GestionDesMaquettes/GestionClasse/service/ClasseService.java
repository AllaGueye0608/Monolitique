package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.model.Classe;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.repository.ClasseRepository;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.service.MaquetteService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ClasseService {
    @Autowired
    private ClasseRepository classeRepository;
    @Autowired
    private MaquetteService maquetteService;

    public Classe create(Classe classe){
        return classeRepository.save(classe);
    }


    public Classe update(Classe classe){
        Classe existing = classeRepository.findById(classe.getId()).get();
        if(existing == null){
            return null;
        }else {
            existing.setNiveau(classe.getNiveau());
            return classeRepository.save(existing);
        }
    }

    public void delete(Classe classe){
        if(classeRepository.findById(classe.getId()).get() != null){
            classeRepository.delete(classe);
        }
    }

    public List<Classe> findAll(){
        return classeRepository.findAll();
    }

    public List<Classe> findByNiveau(int niveau){
        return classeRepository.findByNiveau(niveau);
    }

    public Classe findById(Long id){
        return classeRepository.findById(id).get();
    }
    public void addMaquetteToClasse(Classe classe, Maquette maquette){
        Classe classeExisting = findById(classe.getId());
        Maquette maquetteExisting = maquetteService.findByNom(maquette.getNom(),maquette.getSemestre());

        if(maquetteExisting == null){
           maquetteExisting = maquetteService.create(maquette);
        }
        if(classeExisting != null && maquetteExisting != null){
            System.out.println("Classe et maquette non null");
            List<Maquette> maquettes = classeExisting.getMaquettes();
            if(maquettes == null){
                maquettes = new ArrayList<>();
            }

            if(!maquettes.contains(maquetteExisting)){
                maquettes.add(maquetteExisting);
                maquetteExisting.setClasse(classeExisting);
                classeExisting.setMaquettes(maquettes);
                classeRepository.save(classeExisting);
                maquetteService.update(maquetteExisting);
            }

        }else{
            System.out.println("classe ou maquette null");
        }
    }


    public void archiver(Long id){
        Classe classe = classeRepository.findById(id).get();
        if(classe != null){
            if(classe.isArchive()){
                classe.setArchive(false);
            }else{
                classe.setArchive(true);
            }
        }
    }
}
