package uasz.sn.GestionEnseignement.authentification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uasz.sn.GestionEnseignement.authentification.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,String> {
    public Role findByRole(String role);
}
