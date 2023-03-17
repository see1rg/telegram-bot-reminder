package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.SendMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pro.sky.telegrambot.services.TaskService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TelegramBotUpdatesListenerTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private TaskService taskService;
    @InjectMocks
    private TelegramBotUpdatesListener telegramBotUpdatesListener;


    @Test
    public void testProcessWithValidTask() throws URISyntaxException, IOException {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("07.03.2023 18:00 Позвонить Анне");
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1234L);
        User user = mock(User.class);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(5678L);
        LocalDateTime deadline = LocalDateTime.of(2023, 3, 7, 18, 0);
        telegramBotUpdatesListener.accept(update);
        verify(taskService).save(argThat(task -> task.getChatId() == 1234L
                && task.getUserId() == 5678
                && task.getTask().equals("Позвонить Анне")
                && task.getDeadline().isEqual(deadline)));

        String json = Files.readString(Path.of(TelegramBotUpdatesListenerTest.class.getResource("update.json").toURI())); //todo json file & /start
        Update update1 = BotUtils.fromJson(json.replace("%text%", "/start"), Update.class);

        telegramBotUpdatesListener.process(Collections.singletonList(update1));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();

        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update1.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update1.message().chat().id());
    }

    @Test
    public void testProcessWithInvalidTaskFormat() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("07-03-2023 18:00 Позвонить Анне");
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1234L);
        telegramBotUpdatesListener.accept(update);
        verify(taskService, never()).save(any());
    }

    @Test
    public void testProcessWithNonTextMessage() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        when(message.photo()).thenReturn(new ArrayList<>());// todo picture add
        when(message.sticker()).thenReturn(mock(Sticker.class));
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1234L);
        telegramBotUpdatesListener.accept(update);
        verify(taskService, never()).save(any());
    }
}
