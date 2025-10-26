package ru.kuznetsov.shop.operation.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import ru.kuznetsov.shop.operation.service.OperationService;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OperationCleanUpScheduler {

    @Value("${operation.ttl}")
    private long operationTTL;

    private final ThreadPoolTaskScheduler taskScheduler;
    private final OperationService operationService;

    @PostConstruct
    public void cleanup() {
        taskScheduler.schedule(
                operationService::removeOldOperations,
                Instant.ofEpochSecond(operationTTL)
        );
    }
}
