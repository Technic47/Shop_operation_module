package ru.kuznetsov.shop.operation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kuznetsov.shop.operation.dto.OperationRequest;
import ru.kuznetsov.shop.represent.dto.OperationDto;
import ru.kuznetsov.shop.represent.dto.OperationPayloadDto;
import ru.kuznetsov.shop.represent.enums.OperationType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static ru.kuznetsov.shop.represent.enums.OperationType.SAVE;


@Service
public class OperationService {

    @Value("${operation.timeout}")
    private long waitingForOperationTime;

    @Getter
    private final Map<OperationDto, List<OperationPayloadDto>> operations = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();
    Logger logger = LoggerFactory.getLogger(OperationService.class);

    public boolean containsOperation(String operationId) {
        return operations.keySet().stream()
                .map(OperationDto::getId)
                .anyMatch(operationId::equals);
    }

    public OperationDto getOperation(String operationId) {
        return operations.keySet().stream()
                .filter(operation -> operation.getId().equals(operationId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Operation with " + operationId + " not found"));
    }

    public List<OperationPayloadDto> getOperationData(String operationId) {
        return getOperationData(
                operations.keySet()
                        .stream()
                        .filter(operation -> operation.getId().equals(operationId))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Operation with id " + operationId + " not found"))
        );
    }

    public List<OperationPayloadDto> getOperationData(OperationDto operation) {
        return operations.get(operation);
    }

    public void removeOperations(List<OperationDto> operationIds) {
        for (OperationDto operation : operationIds) {
            removeOperation(operation);
        }
    }

    public void removeOperation(OperationDto operation) {
        operations.remove(operation);
    }

    public void putToSuccessfulOperations(String objectJson,
                                          byte[] operationId,
                                          OperationType type) {
        logger.info("Adding {} to successfully save operation with operationId: {}", objectJson, operationId);

        putOperationToMapInternal(objectJson, operationId, type, 1);
    }

    public void putToFailedOperations(String objectJson,
                                      byte[] operationId,
                                      OperationType type) {
        logger.info("Adding {} to failed save operation with operationId: {}", objectJson, operationId);

        putOperationToMapInternal(objectJson, operationId, type, 0);
    }

    public void addOperation(OperationRequest operationRequest) {
        putOperationToMapInternal(
                operationRequest.getObjectJson(),
                operationRequest.getOperationId(),
                operationRequest.getOperationType(),
                operationRequest.getResult()
        );
    }

    public void removeOldOperations() {
        for (Map.Entry<OperationDto, List<OperationPayloadDto>> entry : operations.entrySet()) {
            entry.getValue().stream()
                    .max(Comparator.comparing(OperationPayloadDto::getDateTime))
                    .ifPresent(container -> {
                        if (container.getDateTime().isBefore(LocalDateTime.now().minusHours(1))) {
                            removeOperation(entry.getKey());
                        }
                    });
        }
    }

    public List<Long> getEntityIdsByOperationId(String operationId) {
        CompletableFuture<OperationDto> operationWithData = waitForOperation(operationId);
        List<Long> entityIds;

        try {
            OperationDto operation = operationWithData.orTimeout(waitingForOperationTime, TimeUnit.SECONDS).get();
            List<OperationPayloadDto> operationData = getOperationData(operation);
            OperationType operationType = operation.getOperationType();
            int result = operation.getResult();

            if (operationType != SAVE)
                throw new RuntimeException("Invalid operation type received for operation " + operationId);
            if (result != 1) throw new RuntimeException("Operation is not success. Id " + operationId);

            entityIds = operationData.stream()
                    .map(OperationPayloadDto::getPayloadId)
                    .toList();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return entityIds;
    }

    private void putOperationToMapInternal(
            String objectJson,
            byte[] operationId,
            OperationType type,
            int result) {
        String operationIdDecoded = new String(operationId);

        putOperationToMapInternal(objectJson, operationIdDecoded, type, result);
    }

    private void putOperationToMapInternal(
            String objectJson,
            String operationId,
            OperationType type,
            int result) {
        try {
            Long id = objectMapper.readTree(objectJson).get("id").longValue();
            OperationDto operation = new OperationDto(operationId, type, result);
            OperationPayloadDto container = new OperationPayloadDto(id, LocalDateTime.now());

            if (operations.containsKey(operation)) {
                operations.get(operation).add(container);
            } else {
                List<OperationPayloadDto> containers = new ArrayList<>();
                containers.add(container);
                operations.put(operation, containers);
            }
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<OperationDto> waitForOperation(String operationId) {
        return CompletableFuture.supplyAsync(() -> {
            while ((!containsOperation(operationId))) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return getOperation(operationId);
        });
    }
}
