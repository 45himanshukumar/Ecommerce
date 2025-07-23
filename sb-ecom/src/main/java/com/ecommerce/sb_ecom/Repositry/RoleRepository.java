package com.ecommerce.sb_ecom.Repositry;

import com.ecommerce.sb_ecom.Model.AppRole;
import com.ecommerce.sb_ecom.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByRoleName(AppRole appRole);
}
