package pro.sky.telegrambot.task;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationTaskScheduler {

    private final TelegramBot telegramBot;
    private final NotificationTaskRepository notificationTaskRepository;


    @Scheduled(fixedRate = 60_000)
    public void checkTasks() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> tasks = notificationTaskRepository.findTasksForNotification(now);
        for (NotificationTask task : tasks) {
            telegramBot.execute(new SendMessage(task.getChatId(), task.getText()));
            notificationTaskRepository.delete(task);
        }
    }
}
