package ru.kuznetsov.shop.operation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.shop.operation.api.OperationControllerApi;
import ru.kuznetsov.shop.operation.dto.OperationRequest;
import ru.kuznetsov.shop.operation.service.OperationService;
import ru.kuznetsov.shop.represent.dto.OperationDto;
import ru.kuznetsov.shop.represent.dto.OperationPayloadDto;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/operation")
@RequiredArgsConstructor
public class OperationController implements OperationControllerApi {

    private final OperationService operationService;

    @GetMapping("/{id}/contains")
    public ResponseEntity<Boolean> containsOperation(@PathVariable String id) {
        return ResponseEntity.ok(operationService.containsOperation(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperationDto> getOperation(@PathVariable String id) {
        try {
            return ResponseEntity.ok(operationService.getOperation(id));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/payload/{id}")
    public ResponseEntity<List<OperationPayloadDto>> getOperationData(@PathVariable String id) {
        try {
            return ResponseEntity.ok(operationService.getOperationData(id));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/payload/{id}/wait")
    public ResponseEntity<List<Long>> getOperationDataWait(@PathVariable String id) {
        try {
            return ResponseEntity.ok(operationService.getEntityIdsByOperationId(id));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).build();
        }
    }

    @PostMapping()
    public void addOperation(@RequestBody OperationRequest operationRequest) {
        operationService.addOperation(operationRequest);
    }

    @PostMapping("/payload")
    public ResponseEntity<List<OperationPayloadDto>> getOperationData(@RequestBody OperationDto operation) {
        List<OperationPayloadDto> operationData = operationService.getOperationData(operation);
        return operationData.isEmpty() ?
                ResponseEntity.status(NO_CONTENT).build()
                : ResponseEntity.ok(operationData);
    }

    @DeleteMapping("/batch")
    public void deleteOperationBatch(@RequestBody List<OperationDto> operationDtoList) {
        operationService.removeOperations(operationDtoList);
    }

    @DeleteMapping
    public void deleteOperation(@RequestBody OperationDto operationDto) {
        operationService.removeOperation(operationDto);
    }
}
