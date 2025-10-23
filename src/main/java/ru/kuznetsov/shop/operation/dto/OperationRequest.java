package ru.kuznetsov.shop.operation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.kuznetsov.shop.represent.enums.OperationType;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OperationRequest {
    private String objectJson;
    private String operationId;
    private OperationType operationType;
    private Integer result;
}
