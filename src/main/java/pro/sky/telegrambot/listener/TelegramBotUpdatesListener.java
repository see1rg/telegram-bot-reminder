package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import pro.sky.telegrambot.models.NotificationTask;
import pro.sky.telegrambot.services.TaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot telegramBot;
    private final TaskService taskService;

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
        updates.forEach(this::accept);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void accept(Update update) {
        logger.info("Processing update: {}", update);
        //01.01.2022 20:00 Сделать домашнюю работу

        String message = update.message().text();
        final Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = PATTERN.matcher(message);
        if (matcher.matches()) {
            String date = matcher.group(1);
            String item = matcher.group(3);
            final LocalDateTime PARSEDATE = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            // Создаем объект Task и заполняем его поля
            NotificationTask task = new NotificationTask();
            task.setId(update.message().messageId());
            task.setChatId(update.message().chat().id());
            task.setTask(item);
            task.setDeadline(PARSEDATE);
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
