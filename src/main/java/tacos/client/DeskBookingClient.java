package tacos.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class DeskBookingClient {

    private static final ReentrantLock lock = new ReentrantLock(); // unfair

    public void sendDeskBookingRequest() {
        lock.lock();
        try {
            // block until lock obtained
            log.info("Lock obtained.");

            // TODO implementation

        } finally {
            lock.unlock();
        }
    }
}
