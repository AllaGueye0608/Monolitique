package uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Seance;
import uasz.sn.GestionEnseignement.GestionDesSalles.model.Salle;
import uasz.sn.GestionEnseignement.users.model.Enseignant;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SeanceRepository extends JpaRepository<Seance,Long> {
    public Seance findByEnseignementAndEnseignant(Enseignement enseignement, Enseignant enseignant);
    @Query("SELECT s FROM Seance s WHERE s.jour = :jour AND s.salle = :salle")
    List<Seance> findByJourAndSalle(@Param("jour") String jour, @Param("salle") Salle salle);
    List<Seance> findByEnseignant(Enseignant enseignant);
    List<Seance> findByEnseignementAndType(Enseignement enseignement,String type);
    List<Seance> findByEnseignement(Enseignement enseignement);

    List<Seance> findByJourAndHeureDebutAndHeureFin(String jour,LocalTime heureDebut,LocalTime heureFin);
    List<Seance> findByJourAndHeureDebutAndHeureFinAndEnseignementAndType(String jour,LocalTime heureDebut,LocalTime heureFin,Enseignement enseignement,String type);
}
