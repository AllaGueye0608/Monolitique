package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;

@Repository
public interface MaquetteRepository extends JpaRepository<Maquette,Long> {
    @Query("SELECT m FROM Maquette m WHERE m.nom = :nom AND m.semestre = :semestre")
    public Maquette findByNomAndSemestre(@Param("nom") String nom,@Param("semestre") int semestre);
}
