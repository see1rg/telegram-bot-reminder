package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.telegrambot.models.NotificationTask;
import pro.sky.telegrambot.repositories.TaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);


    private final TelegramBot telegramBot;
    @Autowired
    private final TaskRepository taskRepository;

    public TelegramBotUpdatesListener(TaskRepository taskRepository, TelegramBot telegramBot ) {
        this.telegramBot = telegramBot;
        this.taskRepository = taskRepository;
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

    private BaseResponse sendMessage(long chatId, String text) {
        SendMessage request = new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true);
        return telegramBot.execute(request);
    }

    private void accept(Update update) {
        logger.info("Processing update: {}", update);
        //01.01.2022 20:00 Сделать домашнюю работу

        String message = update.message().text();
        Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.matches()) {
            String date = matcher.group(1);
            String item = matcher.group(3);
            LocalDateTime parseDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            // Создаем объект Task и заполняем его поля
            NotificationTask task = new NotificationTask();
            task.setId(update.message().messageId());
            task.setChatId(update.message().chat().id());
            task.setTask(item);
            task.setDeadline(parseDate);
            // Сохраняем задачу в базе данных
            taskRepository.save(task);
            SendMessage confirmMessage = new SendMessage(task.getChatId(),
                    "Новое задание добавлено:\n" + task.getTask()
                            + " на дату: \n" + task.getDeadline());
            telegramBot.execute(confirmMessage);
        }

    }
}
