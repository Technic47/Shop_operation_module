package ru.kuznetsov.shop.operation.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.kuznetsov.shop.operation.service.OperationService;
import ru.kuznetsov.shop.represent.dto.*;

import static ru.kuznetsov.shop.represent.common.KafkaConst.*;
import static ru.kuznetsov.shop.represent.enums.OperationType.SAVE;

@Component
@RequiredArgsConstructor
public class SaveOperationListener {

    private final OperationService operationService;

    @KafkaListener(topics = ADDRESS_SAVE_SUCCESSFUL_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void addAddressToSuccessful(String dto, @Header(OPERATION_ID_HEADER) byte[] operationId) {
        operationService.putToSuccessfulOperations(dto, operationId, SAVE);
    }

    @KafkaListener(topics = ADDRESS_SAVE_FAIL_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void addAddressToFail(String dto, @Header(OPERATION_ID_HEADER) byte[] operationId) {
        operationService.putToFailedOperations(dto, operationId, SAVE);
    }

    @KafkaListener(topics = PRODUCT_SAVE_SUCCESSFUL_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void addProductToSuccessful(String dto, @Header(OPERATION_ID_HEADER) byte[] operationId) {
        operationService.putToSuccessfulOperations(dto, operationId, SAVE);
    }

    @KafkaListener(topics = PRODUCT_SAVE_FAIL_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void addProductToFailed(String dto, @Header(OPERATION_ID_HEADER) byte[] operationId) {
        operationService.putToFailedOperations(dto, operationId, SAVE);
    }

    @KafkaListener(topics = PRODUCT_CATEGORY_SAVE_SUCCESSFUL_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void addProductCategoryToSuccessful(String dto, @Header(OPERATION_ID_HEADER) byte[] operationId) {
        operationService.putToSuccessfulOperations(dto, operationId, SAVE);
    }

    @KafkaListener(topics = PRODUCT_CATEGORY_SAVE_FAIL_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void addProductCategoryToFailed(String dto, @Header(OPERATION_ID_HEADER) byte[] operationId) {
        operationService.putToFailedOperations(dto, operationId, SAVE);
    }

    @KafkaListener(topics = STOCK_SAVE_SUCCESSFUL_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void addStockToSuccessful(String dto, @Header(OPERATION_ID_HEADER) byte[] operationId) {
        operationService.putToSuccessfulOperations(dto, operationId, SAVE);
    }

    @KafkaListener(topics = STOCK_SAVE_FAIL_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void addStockToFailed(String dto, @Header(OPERATION_ID_HEADER) byte[] operationId) {
        operationService.putToFailedOperations(dto, operationId, SAVE);
    }

    @KafkaListener(topics = STORE_SAVE_SUCCESSFUL_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void addStoreToSuccessful(String dto, @Header(OPERATION_ID_HEADER) byte[] operationId) {
        operationService.putToSuccessfulOperations(dto, operationId, SAVE);
    }

    @KafkaListener(topics = STORE_SAVE_FAIL_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void addStoreToFailed(String dto, @Header(OPERATION_ID_HEADER) byte[] operationId) {
        operationService.putToFailedOperations(dto, operationId, SAVE);
    }
}
