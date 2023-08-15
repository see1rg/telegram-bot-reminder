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
    private int id;
    @Column(name = "chat_id",nullable = false)
    private long chatId;
    @Column(name = "user_id",nullable = false)
    private long userId;
    @Column(nullable = false)
    private String task;
    @Column(nullable = false)
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
