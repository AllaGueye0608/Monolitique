package uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Seance;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.repository.SeanceRepository;
import uasz.sn.GestionEnseignement.GestionDesSalles.model.Salle;
import uasz.sn.GestionEnseignement.users.model.Enseignant;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Transactional
@Service
public class SeanceService {
    @Autowired
    private SeanceRepository seanceRepository;

    public Seance create(Seance seance){
            return seanceRepository.save(seance);
    }

    public Seance update(Seance seance){
        Seance seanceExisting = seanceRepository.findByEnseignementAndEnseignant(seance.getEnseignement(),seance.getEnseignant());
        if(seanceExisting == null){
            return null;
        }else{
            return seanceRepository.save(seance);
        }
    }

    public void delete(Seance seance){
        seanceRepository.delete(seance);
    }
    public boolean verifierSeance(String jour, LocalTime heureDebut, LocalTime heureFin, Salle salle) {
        List<Seance> seancesExistantes = seanceRepository.findByJourAndSalle(jour, salle);

        for (Seance seance : seancesExistantes) {
            // Vérifie les chevauchements
            if (heureDebut.isBefore(seance.getHeureFin()) && heureFin.isAfter(seance.getHeureDebut())) {
                return true; // Il y a un conflit
            }
        }
        return false;
    }

    public List<Seance> findByJourAndHeureDebutAndHeureFin(String jour,LocalTime heureDebut,LocalTime heureFin){
        return seanceRepository.findByJourAndHeureDebutAndHeureFin(jour,heureDebut,heureFin);
    }

    public List<Seance> findByJourAndHeureDebutAndHeureFinAndEnseignementAndType(String jour,LocalTime heureDebut,LocalTime heureFin,Enseignement enseignement,String type){
        return seanceRepository.findByJourAndHeureDebutAndHeureFinAndEnseignementAndType(jour, heureDebut, heureFin, enseignement, type);
    }
    public boolean verifierSeanceEnseignement(String jour, LocalTime heureDebut, LocalTime heureFin, Enseignement enseignement) {
        // Récupérer toutes les séances pour l'enseignement donné
        List<Seance> seances = seanceRepository.findByEnseignement(enseignement);

        // Vérifier les conflits d'horaire
        for (Seance seance : seances) {
            if (seance.getJour().equals(jour)) {
                // Vérifier si les heures se chevauchent
                if (heureDebut.isBefore(seance.getHeureFin()) && heureFin.isAfter(seance.getHeureDebut())) {
                    return true; // Conflit détecté
                }
            }
        }

        return false; // Aucun conflit détecté
    }

    public List<Seance> findAll(){
        return seanceRepository.findAll();
    }

    public Seance findById(Long id) {
        return seanceRepository.findById(id).orElse(null); // Si l'ID n'est pas trouvé, retourne null
    }

    public Seance findByEnseignementAndEnseignant(Enseignement enseignement, Enseignant enseignant){
        return seanceRepository.findByEnseignementAndEnseignant(enseignement,enseignant);
    }

}
