package ru.kuznetsov.shop.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.context.ActiveProfiles;
import ru.kuznetsov.shop.operation.service.OperationService;
import ru.kuznetsov.shop.represent.dto.AddressDto;
import ru.kuznetsov.shop.represent.dto.OperationDto;
import ru.kuznetsov.shop.represent.enums.OperationType;

import java.util.UUID;

import static ru.kuznetsov.shop.represent.enums.OperationType.SAVE;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AbstractTest {

    protected final OperationService operationService = new OperationService();
    protected ObjectMapper om = new ObjectMapper();

    protected final static String API_PATH = "/operation";
    protected final String operationIdString = UUID.randomUUID().toString();
    protected final Long testEntityId = 666L;
    protected final OperationType operationType = SAVE;
    protected final int operationResult = 1;

    protected AddressDto getMockDto() {
        AddressDto dto = new AddressDto();
        dto.setCity("Test");
        dto.setStreet("Test");
        dto.setHouse("123");

        return dto;
    }

    protected OperationDto getMockOperationDto() {
        OperationDto operationDto = new OperationDto();
        operationDto.setId(operationIdString);
        operationDto.setOperationType(operationType);
        operationDto.setResult(operationResult);

        return operationDto;
    }
}
