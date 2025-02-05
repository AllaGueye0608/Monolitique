package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.model.Classe;

import java.util.List;

@Repository
public interface ClasseRepository extends JpaRepository<Classe,Long> {
    public  List<Classe> findByNiveau(int niveau);
}