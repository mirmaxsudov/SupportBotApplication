package uz.pdp.abdurahmonsupportbot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.pdp.abdurahmonsupportbot.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.chatId = :chatId")
    User findByChatId(@Param("chatId") Long chatId);

    @Query("select u from User u where u.userRole = 'ADMIN'")
    User findByAdmin();
}
