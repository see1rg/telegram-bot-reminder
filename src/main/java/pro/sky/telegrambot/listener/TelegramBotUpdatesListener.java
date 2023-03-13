package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Controller;
import pro.sky.telegrambot.models.NotificationTask;
import pro.sky.telegrambot.services.TaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j
@Controller
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final TaskService taskService;
    private static final Pattern TASK_PATTERN = Pattern.compile("([\\d\\\\.:\\s]{16})(\\s)([А-яA-z\\s\\d,.!?;]+)");

    public TelegramBotUpdatesListener(TelegramBot telegramBot, TaskService taskService) {
        this.telegramBot = telegramBot;
        this.taskService = taskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(this::accept);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void accept(Update update) {
        log.debug("Processing update: " + update);
        String message = update.message().text();

        Matcher matcher = TASK_PATTERN.matcher(message);
        if (matcher.matches()) {
            String date = matcher.group(1);
            String item = matcher.group(3);
            final LocalDateTime parseDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            // Создаем объект Task и заполняем его поля
            NotificationTask task = new NotificationTask();
            task.setId(update.message().messageId());
            task.setChatId(update.message().chat().id());
            task.setTask(item);
            task.setDeadline(parseDate);
            task.setUserId(update.message().from().id());
            // Сохраняем задачу в базе данных
            taskService.save(task);
            SendMessage confirmMessage = new SendMessage(task.getChatId(),
                    "Новое задание добавлено:\n" + task.getTask()
                            + "\n на дату: \n" + task.getDeadline());
            telegramBot.execute(confirmMessage);
        }
    }
}
