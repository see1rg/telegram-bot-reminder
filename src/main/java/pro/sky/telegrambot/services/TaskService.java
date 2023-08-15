package pro.sky.telegrambot.services;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.models.NotificationTask;
import pro.sky.telegrambot.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Log4j
@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public void save(NotificationTask task) {
        log.debug("Requesting to save the task: " + task);
        taskRepository.save(task);
    }

    @Transactional
    public void delete(NotificationTask task) {
        log.debug("Requesting to delete the task:" + task);
        taskRepository.delete(task);
    }

    public List<NotificationTask> tasksOnTime() {
        log.debug("Requesting tasks on time: " + LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        return taskRepository
                .findByDeadline(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }
}
