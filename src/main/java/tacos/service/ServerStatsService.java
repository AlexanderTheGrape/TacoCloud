package tacos.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tacos.client.ServerStatisticsClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static tacos.Constants.ZONE_ID_SYDNEY;

@Service
@Slf4j
public class ServerStatsService {

    private static final AtomicInteger visitsToday = new AtomicInteger(); // Initializes to 0
    private static ScheduledExecutorService executorService;
    @Autowired
    private ServerStatisticsClient serverStatisticsClient;

    private ServerStatsService() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        persistAndResetSiteVisitStatsDaily();
    }

    public int incrementVisitsCount() {
        return visitsToday.addAndGet(1);
    }

    public static void persistAndResetSiteVisitStatsDaily() {
        // Create a Runnable or Callable of stuff to be done
        int visits = visitsToday.get();

        // TODO Persist this statistic
        performTaskDailyAtMidnight(() -> {
                log.info("Site visits for day ended: {}. No implementation to persist data.", visits);
                visitsToday.set(0);
        });

        // TODO Calculate statistic for last x days, update in memory (variable here in service)

        // TODO Update view with this statistic
    }

    private static void performTaskDailyAtMidnight(Runnable task) {
        // Obtain current time
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZONE_ID_SYDNEY);
        ZonedDateTime startOfTomorrow = ZonedDateTime.of(LocalDate.now().plusDays(1),
                LocalTime.MIDNIGHT, ZONE_ID_SYDNEY);

        long initialDelaySeconds = now.until(startOfTomorrow, ChronoUnit.SECONDS);
        long delaySeconds = 60 * 60 * 24; // i.e. every 24 hours

        // Schedule task(s) for 00:00am using time until that time next day
        executorService.scheduleWithFixedDelay(task, initialDelaySeconds, delaySeconds, TimeUnit.SECONDS);
    }
}
