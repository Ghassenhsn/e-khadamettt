package tn.ekhadamet.ekhadamet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.ekhadamet.ekhadamet.Entities.Role;
import tn.ekhadamet.ekhadamet.Entities.RoleName;


import java.util.Optional;

public interface RoleRepository  extends JpaRepository<Role, Long> {


    Optional<Object> findByRoleName(RoleName attr0);
}
