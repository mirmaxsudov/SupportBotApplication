package uz.pdp.abdurahmonsupportbot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message")
@SuppressWarnings("JpaDataSourceORMInspection")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Size(max = 255000)
    @Column(length = 250000)
    private String message;
    private Long chatId;
    private int messageId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}