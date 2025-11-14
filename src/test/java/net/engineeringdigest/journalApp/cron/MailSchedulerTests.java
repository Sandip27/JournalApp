package net.engineeringdigest.journalApp.cron;

import net.engineeringdigest.journalApp.scheduler.MailScheduler;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailSchedulerTests {
    @Autowired
    private MailScheduler mailScheduler;

    @Disabled
    @Test
    public void testFetchUserAndSendMail() {
        mailScheduler.fetchUsersAndSendSAMail();
    }

}
