package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);


    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

//    @Override
//    public int process(List<Update> updates) {
//        updates.forEach(update -> {
//            logger.info("Processing update: {}", update);
//            // Process your updates here
//        });
//        return UpdatesListener.CONFIRMED_UPDATES_ALL;
//    }

    private void setCommands() {
        BotCommand[] commands = new BotCommand[] {
                new BotCommand("start", "Start the bot"),
                new BotCommand("help", "Show help")
        };
        SetMyCommands setCommands = new SetMyCommands(commands);
        telegramBot.execute(setCommands);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message() != null && update.message().text() != null) {
                String text = update.message().text().trim();
                long chatId = update.message().chat().id();

                String[] messageParts = update.message().text().split(" ");
                String dateStr = messageParts[0];
                String timeStr = messageParts[1];
                String event = text.substring(text.indexOf(" ", text.indexOf(" ") + 1) + 1);

                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.parse(dateStr + " " + timeStr);
                } catch (DateTimeParseException e) {
                    sendMessage(update.message().chat().id(), "Пожалуйста, введите дату и время в формате 'год-месяц-день час:минуты'. Например: '2023-03-07 15:30'");
                    return;
                }

                switch (text) {
                    case "/start":
                        sendMessage(chatId, "Hello! I'm your bot. How can I assist you?");
                        break;
                    case "/help":
                        sendMessage(chatId, "This is the help message.");
                        break;
                    case "/add_task":

                    default:
                        sendMessage(chatId, "Sorry, I didn't understand that.");
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }



    @Override
    public void process(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String[] messageParts = messageText.split(" ");
            if (messageParts.length != 3) {
                sendMessage(update.getMessage().getChatId(), "Пожалуйста, введите дату, время и событие в формате 'год-месяц-день час:минуты событие'. Например: '2023-03-07 15:30 Встреча с друзьями'");
                return;
            }
            String dateStr = messageParts[0];
            String timeStr = messageParts[1];
            String event = messageParts[2];
            if (dateStr.isEmpty() || timeStr.isEmpty() || event.isEmpty()) {
                sendMessage(update.getMessage().getChatId(), "Пожалуйста, введите дату, время и событие.");
                return;
            }
            LocalDateTime dateTime;
            try {
                dateTime = LocalDateTime.parse(dateStr + " " + timeStr);
            } catch (DateTimeParseException e) {
                sendMessage(update.getMessage().getChatId(), "Пожалуйста, введите дату и время в формате 'год-месяц-день час:минуты'. Например: '2023-03-07 15:30'");
                return;
            }
            sendMessage(update.getMessage().getChatId(), "Событие '" + event + "' успешно добавлено в календарь на " + dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
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
