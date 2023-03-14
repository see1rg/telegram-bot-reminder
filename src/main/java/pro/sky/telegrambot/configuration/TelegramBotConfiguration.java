package pro.sky.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class TelegramBotConfiguration {

    @Value("${telegram.bot.name}")
    private String name;

    @Value("${telegram.bot.token}")
    private String token;

    @Bean
    public TelegramBot telegramBot() {
        //возвращает экземпляр TelegramBot, созданный с помощью значения поля token.
        TelegramBot bot = new TelegramBot(token);
        //удаляет все команды бота с помощью метода execute() и класса DeleteMyCommands.
        bot.execute(new DeleteMyCommands());
        return bot;
    }

}
