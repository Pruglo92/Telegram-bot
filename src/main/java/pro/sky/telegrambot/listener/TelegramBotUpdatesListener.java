package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotUpdatesListener implements UpdatesListener {

    private boolean isBotReady = false;
    private final TelegramBot telegramBot;
    private final NotificationTaskRepository notificationTaskRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    @Transactional
    public int process(final List<Update> updates) {
        updates.forEach(update -> {
            log.info("Processing update: {}", update);
            Message message = update.message();
            if (message != null) {
                if (!isBotReady) {
                    if (handleStartCommandIfStart(message)) {
                        isBotReady = true;
                        return;
                    } else {
                        telegramBot.execute(new SendMessage(message.chat().id(), "Запустите бота командой /start"));
                        return;
                    }
                } else if (handleStopCommandIfStop(message)) {
                    isBotReady = false;
                    return;
                } else {
                    processUserMessage(message);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void processUserMessage(final Message message) {
        if (message.text() != null) {
            if (message.text().matches("\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2} .*")) {
                String datePart = message.text().substring(0, 16);
                String taskText = message.text().substring(17);
                LocalDateTime sentDate = LocalDateTime.parse(datePart, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                if (sentDate.isBefore(LocalDateTime.now())) {
                    log.warn("Wrong time entered : {}", sentDate);
                    telegramBot.execute(new SendMessage(message.chat().id(), "Время уведомления должно быть в будущем."));
                } else {
                    NotificationTask task = new NotificationTask();
                    task.setText(taskText);
                    task.setSentDate(sentDate);
                    task.setChatId(message.chat().id());
                    notificationTaskRepository.save(task);
                    telegramBot.execute(new SendMessage(message.chat().id(), "Задача уведомления успешно добавлена"));
                    log.info("Task '{}' scheduled for {}", taskText, sentDate);
                }
            } else {
                log.warn("Wrong message format : {}", message.text());
                telegramBot.execute(new SendMessage(message.chat().id(), "Неправильный формат сообщения"));
            }
        }
    }

    private boolean handleStartCommandIfStart(final Message message) {
        if ("/start".equals(message.text())) {
            handleStartCommand(message);
            return true;
        }
        return false;
    }

    private boolean handleStopCommandIfStop(final Message message) {
        if ("/stop".equals(message.text())) {
            handleStopCommand(message);
            return true;
        }
        return false;
    }

    private void handleStartCommand(final Message message) {
        log.info("Bot received the /start command. Inclusion...");

        String response = "Привет! Я бот, который поможет тебе запланировать уведомления. " +
                "Отправь мне сообщение в формате 'дд.мм.гггг чч:мм текст задачи', " +
                "и я напомню тебе о ней в указанное время.";
        telegramBot.execute(new SendMessage(message.chat().id(), response));
    }

    private void handleStopCommand(final Message message) {
        log.info("Bot received /stop command. Shutting down...");

        String response = "Бот выключен. Для включения бота отправьте команду /start.";
        telegramBot.execute(new SendMessage(message.chat().id(), response));
    }
}