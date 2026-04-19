package com.feelsent.repository;

import com.feelsent.model.Notification;
import com.feelsent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);

    // Viena SQL UPDATE užklausa vietoj N atskirų save() – efektyvu esant daug neperskaitytų
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    void markAllReadByUser(@Param("user") User user);

    boolean existsByUserAndIsReadFalse(User user);
}