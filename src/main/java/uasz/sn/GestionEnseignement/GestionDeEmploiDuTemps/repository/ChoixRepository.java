package uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Choix;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.users.model.Enseignant;

import java.util.List;

@Repository
public interface ChoixRepository extends JpaRepository<Choix,Long> {
    public List<Choix> findByEnseignant(Enseignant enseignant);
    public Choix findByEnseignantAndEnseignement(Enseignant enseignant, Enseignement enseignement);
    public Choix findByEnseignement(Enseignement enseignement);
}
