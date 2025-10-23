package ru.kuznetsov.shop.operation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuznetsov.shop.operation.dto.OperationRequest;
import ru.kuznetsov.shop.operation.service.OperationService;
import ru.kuznetsov.shop.represent.dto.OperationDto;
import ru.kuznetsov.shop.represent.dto.OperationPayloadDto;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/operation")
@RequiredArgsConstructor
public class OperationController {

    private final OperationService operationService;

    @GetMapping("/{id}/contains")
    public ResponseEntity<Boolean> containsOperation(@PathVariable String id) {
        return ResponseEntity.ok(operationService.containsOperation(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<OperationDto>> getOperation(@PathVariable String id) {
        return ResponseEntity.ok(operationService.getOperation(id));
    }

    @GetMapping("/payload/{id}/wait")
    public ResponseEntity<List<Long>> getOperationDataWait(@PathVariable String id) {
        return ResponseEntity.ok(operationService.getEntityIdsByOperationId(id));
    }

    @PostMapping()
    public void addOperation(@RequestBody OperationRequest operationRequest) {
        operationService.addOperation(operationRequest);
    }

    @PostMapping("/payload/{id}")
    public ResponseEntity<List<OperationPayloadDto>> getOperationData(@PathVariable String id) {
        return ResponseEntity.ok(operationService.getOperationData(id));
    }

    @PostMapping("/payload")
    public ResponseEntity<List<OperationPayloadDto>> getOperationData(@RequestBody OperationDto operation) {
        return ResponseEntity.ok(operationService.getOperationData(operation));
    }

    @DeleteMapping("/batch")
    public void deleteOperation(@RequestBody List<OperationDto> operationDtoList) {
        operationService.removeOperations(operationDtoList);
    }

    @DeleteMapping
    public void deleteOperation(@RequestBody OperationDto operationDto) {
        operationService.removeOperation(operationDto);
    }
}
