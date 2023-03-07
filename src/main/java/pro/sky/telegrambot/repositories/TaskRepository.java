package pro.sky.telegrambot.repositories;

import com.sun.xml.bind.v2.model.core.ID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.models.NotificationTask;

@Repository
public interface TaskRepository extends JpaRepository<NotificationTask, ID> {

}
