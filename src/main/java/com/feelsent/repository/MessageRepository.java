package com.feelsent.repository;

import com.feelsent.enums.MessageStatus;
import com.feelsent.model.Message;
import com.feelsent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // inbox: tik aktyvios žinutės (be REACTED ir EXPIRED), naujausios pirma
    List<Message> findAllByReceiverAndStatusNotInOrderBySentAtDesc(User receiver, List<MessageStatus> excludedStatuses);

    // sent: tik aktyvios žinutės (be REACTED ir EXPIRED), naujausios pirma
    List<Message> findAllBySenderAndStatusNotInOrderBySentAtDesc(User sender, List<MessageStatus> excludedStatuses);

    // žinutės kurios turėtų būti paverstos EXPIRED (neatsakytos ilgiau nei threshold)
    List<Message> findAllByStatusNotInAndSentAtBefore(List<MessageStatus> excludedStatuses, LocalDateTime before);

    // žinutės tarp dviejų vartotojų po konkrečios datos (limito tikrinimui)
    List<Message> findAllBySenderAndReceiverAndSentAtAfter(User sender, User receiver, LocalDateTime after);

    // žinučių skaičius tarp dviejų vartotojų per laikotarpį (limito tikrinimui)
    long countBySenderAndReceiverAndSentAtAfter(User sender, User receiver, LocalDateTime after);

    // visa žinučių istorija tarp dviejų vartotojų (pasikartojimų vengimui)
    List<Message> findAllBySenderAndReceiver(User sender, User receiver);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.sender = :user OR m.receiver = :user")
    void deleteAllByUser(@Param("user") User user);
}
