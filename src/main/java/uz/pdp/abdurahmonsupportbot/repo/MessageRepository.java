package uz.pdp.abdurahmonsupportbot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.pdp.abdurahmonsupportbot.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select m from Message m where m.messageId = :messageId")
    Message findByMessageId(@Param("messageId") Integer messageId);
}