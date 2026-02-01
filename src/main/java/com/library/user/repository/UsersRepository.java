package com.library.user.repository;

import com.library.user.entity.Users;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUsername(String username);

    boolean existsByUsername(String username);

    //===========================================================
    // BEST
    // SELECT *
    //FROM users
    //WHERE email = ? OR username = ?
    //LIMIT 1;

    //SELECT *
    //FROM users
    //WHERE email = 'prathmesh@gmail.com'
    //   OR username = 'prathmesh@gmail.com'
    //LIMIT 1;

    Optional<Users> findByEmailOrUsername(String email, String username);

    // check mobile No
    Optional<Users>findByMobileNo(String mobileNo);

    Optional<Users> findByVerificationCode(String code);

    Page<Users> findByRoles_RoleName(String roleName, Pageable pageable);
}
