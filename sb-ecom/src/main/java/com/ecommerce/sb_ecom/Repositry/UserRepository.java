package com.ecommerce.sb_ecom.Repositry;

import com.ecommerce.sb_ecom.Model.AppRole;
import com.ecommerce.sb_ecom.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
      Optional<User> findByUserName(String username);


      boolean existsByUserName(String username);

      boolean existsByEmail(String email);
      @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :role")
      Page<User> findByRoleName(@Param("role") AppRole role, Pageable pageable);
}


