package com.feelsent.repository;

import com.feelsent.enums.WishTone;
import com.feelsent.model.Wish;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByIdAndActiveTrue(Long id);

    List<Wish> findByToneAndRelationshipTypeAndActiveTrue(WishTone tone, String relationshipType);

    List<Wish> findByRelationshipTypeAndActiveTrue(String relationshipType);

    // Kešuojama – kinta tik kai adminas prideda/deaktyvuoja palinkėjimą (@CacheEvict AdminController)
    @Cacheable("active-wishes")
    List<Wish> findAllByActiveTrue();

    Page<Wish> findAllByActiveTrue(Pageable pageable);

    // Kešuojama pagal toną + ryšio tipą – kviečiama kiekvieno siuntimo metu
    @Cacheable(value = "wishes-by-tone", key = "#tone + '_' + #relType")
    @Query("SELECT w FROM Wish w WHERE w.tone = :tone AND (w.relationshipType = :relType OR w.relationshipType = 'ALL') AND w.active = true")
    List<Wish> findByToneAndRelationshipTypeOrAll(
            @Param("tone") WishTone tone,
            @Param("relType") String relType
    );
}
