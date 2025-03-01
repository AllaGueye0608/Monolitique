package uasz.sn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uasz.sn.model.Repartition;
import uasz.sn.model.Enseignant;
import uasz.sn.model.Enseignement;

import java.util.List;

@RepositoryRestResource
public interface RepartitionRepository extends JpaRepository<Repartition,Long> {
    public List<Repartition> findByEnseignant(Enseignant enseignant);
    public Repartition findByEnseignantAndEnseignementAndType(Enseignant enseignant, Enseignement enseignement, String type);
    public Repartition findByEnseignement(Enseignement enseignement);
    public List<Repartition> findByEnseignementAndType(Enseignement enseignement, String type);
}
