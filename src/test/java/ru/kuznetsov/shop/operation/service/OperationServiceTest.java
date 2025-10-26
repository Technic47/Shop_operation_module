package ru.kuznetsov.shop.operation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.kuznetsov.shop.operation.AbstractTest;
import ru.kuznetsov.shop.operation.dto.OperationRequest;
import ru.kuznetsov.shop.represent.dto.AddressDto;
import ru.kuznetsov.shop.represent.dto.OperationDto;
import ru.kuznetsov.shop.represent.dto.OperationPayloadDto;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class OperationServiceTest extends AbstractTest {

    private final OperationService operationService = new OperationService();

    @Test
    void addOperation() throws JsonProcessingException {
        OperationDto dto = addMockOperation();

        Map<OperationDto, List<OperationPayloadDto>> operations = operationService.getOperations();

        assertTrue(operations.containsKey(dto));
    }

    @Test
    void putToSuccessfulOperations() throws JsonProcessingException {
        AddressDto mockDto = getMockDto();
        mockDto.setId(testEntityId);

        OperationDto mockOperationDto = getMockOperationDto();

        operationService.putToSuccessfulOperations(
                om.writeValueAsString(mockDto),
                mockOperationDto.getId().getBytes(StandardCharsets.UTF_8),
                mockOperationDto.getOperationType()
        );

        testOperationInfo(mockOperationDto, 1);
    }

    @Test
    void putToFailedOperations() throws JsonProcessingException {
        AddressDto mockDto = getMockDto();
        mockDto.setId(testEntityId);

        OperationDto mockOperationDto = getMockOperationDto();
        mockOperationDto.setResult(0);

        operationService.putToFailedOperations(
                om.writeValueAsString(mockDto),
                mockOperationDto.getId().getBytes(StandardCharsets.UTF_8),
                mockOperationDto.getOperationType()
        );

        testOperationInfo(mockOperationDto, 0);
    }

    @Test
    void containsOperationTrue() throws JsonProcessingException {
        OperationDto dto = addMockOperation();

        assertTrue(operationService.containsOperation(dto.getId()));
    }

    @Test
    void containsOperationFalse() {
        assertFalse(operationService.containsOperation(operationIdString));
    }

    @Test
    void getOperation() throws JsonProcessingException {
        OperationDto dto = addMockOperation();

        OperationDto operation = operationService.getOperation(dto.getId());

        assertEquals(dto, operation);
    }

    @Test
    void getOperationFail() {
        assertThrows(RuntimeException.class, () -> operationService.getOperation(operationIdString));
    }

    @Test
    void getOperationData() throws JsonProcessingException {
        OperationDto dto = addMockOperation();

        List<OperationPayloadDto> operationData = operationService.getOperationData(dto.getId());

        operationData.forEach(operation -> assertNotNull(operation.getDateTime()));

        assertTrue(operationData.stream()
                .anyMatch(data -> data.getPayloadId().equals(testEntityId)));
    }

    @Test
    void getOperationDataWithObject() throws JsonProcessingException {
        OperationDto dto = addMockOperation();

        List<OperationPayloadDto> operationData = operationService.getOperationData(dto);

        operationData.forEach(operation -> assertNotNull(operation.getDateTime()));

        assertTrue(operationData.stream()
                .anyMatch(data -> data.getPayloadId().equals(testEntityId)));
    }

    @Test
    void removeOperations() throws JsonProcessingException {
        OperationDto dto = addMockOperation();

        operationService.removeOperations(List.of(dto));

        assertFalse(operationService.getOperations().containsKey(dto));
    }

    @Test
    void removeOperation() throws JsonProcessingException {
        OperationDto dto = addMockOperation();

        operationService.removeOperation(dto);

        assertFalse(operationService.getOperations().containsKey(dto));
    }

    @Test
    void removeOldOperations() throws JsonProcessingException, InterruptedException {
        ReflectionTestUtils.setField(operationService, "operationTTL", 1L);

        OperationDto dto = addMockOperation();

        assertTrue(operationService.getOperations().containsKey(dto));

        sleep(1000);

        operationService.removeOldOperations();

        assertFalse(operationService.getOperations().containsKey(dto));
    }

    @Test
    void clearOperations() throws JsonProcessingException {
        OperationDto dto = addMockOperation();

        assertTrue(operationService.getOperations().containsKey(dto));

        operationService.clearOperations();

        assertTrue(operationService.getOperations().isEmpty());
    }

    @Test
    void getEntityIdsByOperationId() throws Exception {
        ReflectionTestUtils.setField(operationService, "waitingForOperationTime", 100L);
        operationService.clearOperations();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<List<Long>> future = executorService.submit(() -> operationService.getEntityIdsByOperationId(operationIdString));

        executorService.submit(() -> {
            try {
                return addMockOperation();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        assertTrue(future.get().contains(testEntityId));
    }

    @Test
    void getEntityIdsByOperationIdThrows() {
        ReflectionTestUtils.setField(operationService, "waitingForOperationTime", 1L);
        operationService.clearOperations();

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        Future<List<Long>> future = executorService.submit(() -> operationService.getEntityIdsByOperationId(operationIdString));

        assertThrows(ExecutionException.class, future::get);
    }

    private void testOperationInfo(OperationDto mockOperationDto, int result) {
        Map<OperationDto, List<OperationPayloadDto>> operations = operationService.getOperations();

        assertTrue(operations.containsKey(mockOperationDto));

        OperationDto savedOperation = operations.keySet().stream()
                .filter(operationDto -> operationDto.getId().equals(mockOperationDto.getId()))
                .findFirst()
                .get();

        assertEquals(mockOperationDto.getId(), savedOperation.getId());
        assertEquals(mockOperationDto.getOperationType(), savedOperation.getOperationType());
        assertEquals(result, savedOperation.getResult());
    }

    private OperationDto addMockOperation() throws JsonProcessingException {
        AddressDto mockDto = getMockDto();
        mockDto.setId(testEntityId);

        OperationDto mockOperationDto = getMockOperationDto();

        OperationRequest request = new OperationRequest();
        request.setObjectJson(om.writeValueAsString(mockDto));
        request.setOperationId(mockOperationDto.getId());
        request.setOperationType(mockOperationDto.getOperationType());
        request.setResult(mockOperationDto.getResult());

        operationService.addOperation(request);

        return mockOperationDto;
    }

}