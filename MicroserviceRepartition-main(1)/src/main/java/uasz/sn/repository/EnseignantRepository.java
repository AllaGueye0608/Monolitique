package uasz.sn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uasz.sn.model.Enseignant;

@RepositoryRestResource
public interface EnseignantRepository extends JpaRepository<Enseignant,Long> {
}
