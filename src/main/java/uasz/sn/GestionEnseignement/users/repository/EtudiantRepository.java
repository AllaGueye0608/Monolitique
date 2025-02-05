package uasz.sn.GestionEnseignement.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.users.model.Etudiant;
@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant,Long> {
    public Etudiant findByUsername(String username);
    public Etudiant findByMatricule(String matricule);
}
