package com.feelsent.repository;

import com.feelsent.model.PointTransaction;
import com.feelsent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    List<PointTransaction> findAllByUser(User user); // visi vartotojo taškų įrašai

    // @Query – rankinis JPQL užklausimas, nes Spring negali pats sugeneruoti SUM
    @Query("SELECT SUM(p.points) FROM PointTransaction p WHERE p.user = :user")
    Integer sumPointsByUser(@Param("user") User user); // bendras taškų kiekis
}
