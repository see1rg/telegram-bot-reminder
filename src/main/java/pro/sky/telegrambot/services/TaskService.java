package pro.sky.telegrambot.services;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.models.NotificationTask;
import pro.sky.telegrambot.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Log4j
@Component
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public NotificationTask save(NotificationTask task) {
        log.debug("Requesting to save the task: " + task);
        return taskRepository.save(task);
    }

    @Transactional
    public void delete(NotificationTask task) {
        log.debug("Requesting to delete the task:" + task);
        taskRepository.delete(task);
    }

    @Transactional
    public List<NotificationTask> tasksOnTime() {
        log.debug("Requesting tasks on time: " + LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        return taskRepository
                .findByDeadline(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }
}
