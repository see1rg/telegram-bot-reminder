package pro.sky.telegrambot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.models.NotificationTask;
import pro.sky.telegrambot.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Component
public class TaskService {
    private final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public NotificationTask save(NotificationTask task){
        logger.info("Requesting to save the task: {}.", task);
        return taskRepository.save(task);
    }

    public void delete(NotificationTask task){
        logger.info("Requesting to delete the task: {}.", task);
        taskRepository.delete(task);
    }

    public List<NotificationTask> tasksOnTime(){
        logger.info("Requesting tasks on time: {}.", LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
       return taskRepository
                .findByDeadline(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }
}
