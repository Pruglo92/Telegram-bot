package pro.sky.telegrambot.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "notification_tasks")
@Entity
public class NotificationTask {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "chat_id")
    private Long chatId;

    @NonNull
    @Column(name = "text")
    private String text;

    @NonNull
    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(id, that.id) && chatId.equals(that.chatId) && text.equals(that.text) && sentDate.equals(that.sentDate) && Objects.equals(creationDate, that.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, text, sentDate, creationDate);
    }
}
