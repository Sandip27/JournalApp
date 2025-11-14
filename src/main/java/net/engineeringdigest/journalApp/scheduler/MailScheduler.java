package net.engineeringdigest.journalApp.scheduler;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.enums.Sentiment;
import net.engineeringdigest.journalApp.model.SentimentData;
import net.engineeringdigest.journalApp.repository.UserRepositoryImpl;
import net.engineeringdigest.journalApp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@Component
public class MailScheduler {
    @Autowired
    private AppCache appCache;
    @Autowired
    private UserRepositoryImpl userRepositoryImpl;
    @Autowired
    private EmailService emailService;
    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    @Scheduled(cron = "0 0 9 ? * SUN")
    public void fetchUsersAndSendSAMail() {
        List<User> usersForSA = userRepositoryImpl.getUserForSentimentAnalysis();

        for (User user : usersForSA) {
            List<Sentiment> last7DaysSentiments = user.getJournalEntries()
                    .stream()
                    .filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS)))
                    .map(x -> x.getSentiment())
                    .collect(Collectors.toList());

            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for (Sentiment sentiment : last7DaysSentiments) {
                if (sentiment != null)
                    sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            }

            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }

            if (mostFrequentSentiment != null) {
                SentimentData sentimentData = SentimentData.builder().email(user.getEmail()).sentiment("Sentiment for last 7 days " + mostFrequentSentiment).build();
                try {
                    kafkaTemplate.send("weekly-sentiments", sentimentData.getEmail(), sentimentData);
                } catch (Exception e) {
                    emailService.sendEmail(sentimentData.getEmail(), "Sentiment for previous week", sentimentData.toString());
                }
            }
        }
    }
    @Scheduled(cron = "* 0/10 * ? * *")
    public void clearAppCache() {
        appCache.init();
    }
}
