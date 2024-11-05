package tacos.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ServerStatsService {

    private static final AtomicInteger visitsToday = new AtomicInteger();

    public int incrementVisitsCount() {
        return visitsToday.addAndGet(1);
    }
}
