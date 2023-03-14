package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pro.sky.telegrambot.services.TaskService;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TelegramBotUpdatesListenerTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private TaskService taskService;

    private TelegramBotUpdatesListener telegramBotUpdatesListener;

    @Before
    public void setUp() {
        telegramBotUpdatesListener = new TelegramBotUpdatesListener(telegramBot, taskService);
    }

    @Test
    public void testProcessWithValidTask() {
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
        verify(taskService).save(argThat(task -> {
            return task.getChatId() == 1234L
                    && task.getUserId() == 5678
                    && task.getTask().equals("Позвонить Анне")
                    && task.getDeadline().isEqual(deadline);
        }));
        verify(telegramBot).execute(argThat(messageToSend -> {
            return messageToSend.getChatId() == 1234L
                    && messageToSend.getText().contains("Новое задание добавлено:")
                    && messageToSend.getText().contains("Позвонить Анне")
                    && messageToSend.getText().contains("07.03.2023 18:00");
        }));
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
        verify(telegramBot).execute(argThat(messageToSend -> {
            return messageToSend. == 1234L
                    && messageToSend.getText().contains("Неверный формат сообщения.");
        }));
    }

    @Test
    public void testProcessWithNonTextMessage() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        when(message.photo()).thenReturn(new ArrayList<>());
        when(message.sticker()).thenReturn(mock(Sticker.class));
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1234L);
        telegramBotUpdatesListener.accept(update);
        verify(taskService, never()).save(any());
        verify(telegramBot).execute(argThat(messageToSend -> {
            return messageToSend.getChat() == 1234L
                    && messageToSend.get.contains("Извините, но я умею обрабатывать только текст.");
        }));
    }
}
