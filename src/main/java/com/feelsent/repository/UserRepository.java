package com.feelsent.repository;

import com.feelsent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// JpaRepository suteikia: save, findById, findAll, delete ir kt. – nereikia rašyti pačiam
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // ieško vartotojo pagal el. paštą (prisijungimui)

    boolean existsByEmail(String email); // tikrina ar el. paštas jau užregistruotas

    Optional<User> findByVerificationToken(String token);

    // Vartotojai, kurie nesilankė nuo nurodyto laiko – re-engagement tikrinimui (su paginacija)
    List<User> findAllByLastLoginAtBefore(LocalDateTime threshold, Pageable pageable);

    // Atominis taškų prieaugis – apsaugo nuo race condition esant vienalaikėms užklausoms
    @Modifying
    @Query("UPDATE User u SET u.points = u.points + :points WHERE u.id = :userId")
    void incrementPoints(@Param("userId") Long userId, @Param("points") int points);
}