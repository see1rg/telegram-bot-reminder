package pro.sky.telegrambot.schedulers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.models.NotificationTask;
import pro.sky.telegrambot.services.TaskService;

import java.util.List;
@Service
public class NotificationScheduler {
    private final TaskService taskService;
    private final TelegramBot telegramBot;

    public NotificationScheduler(TaskService taskService, TelegramBot telegramBot) {
        this.taskService = taskService;
        this.telegramBot = telegramBot;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    void findTaskCurrentTime() {
        List<NotificationTask> tasksOnTime = taskService.tasksOnTime();
        for (NotificationTask task :
                tasksOnTime) {
            String message = String.format("На текущее время у вас запланировано задание: \n '%s' ", task.getTask());
            sendMessage(task.getChatId(), message);
            taskService.delete(task);
        }
    }

    private BaseResponse sendMessage(long chatId, String text) {
        SendMessage request = new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true);
        return telegramBot.execute(request);
    }
}