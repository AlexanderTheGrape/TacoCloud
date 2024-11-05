package tacos.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledTaskService {
    private static ScheduledExecutorService executorService;

    private ScheduledTaskService() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public static void persistAndResetSiteVisitStatsDaily() {

        // Create a Runnable or Callable of stuff to be done

        // Obtain current time

        // Schedule task(s) for 00:00am using time until that time next day


    }

    private static void persistAndResetSiteVisitStats() {
        // Obtain site visits since last scheduled run
        // static

        // Persist this statistic

        // Calculate statistic for last x days
        // TODO Update view with this statistic
        // Reset site visits counter
    }
}
