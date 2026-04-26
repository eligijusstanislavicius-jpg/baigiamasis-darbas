package com.feelsent.repository;

import com.feelsent.model.UniqueWish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniqueWishRepository extends JpaRepository<UniqueWish, Long> {
}
