package ru.kuznetsov.shop.operation.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.kuznetsov.shop.operation.service.OperationService;

@Component
@RequiredArgsConstructor
public class OperationCleanUpScheduler {

    private final OperationService operationService;

    @Scheduled(cron = "${scheduler.cron}")
    public void cleanup() {
        operationService.removeOldOperations();
    }
}
