package pro.sky.telegrambot.models;

import lombok.*;

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
        if (!(o instanceof NotificationTask)) return false;
        NotificationTask that = (NotificationTask) o;
        return id == that.id && Objects.equals(chatId, that.chatId) && Objects.equals(userId, that.userId) && Objects.equals(task, that.task) && Objects.equals(deadline, that.deadline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, userId, task, deadline);
    }
}
