package pro.sky.telegrambot.models;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notification_task_name", schema = "schema_bot")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTask {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "task")
    private String task;
    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        NotificationTask that = (NotificationTask) o;
        return getId() != 0 && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
