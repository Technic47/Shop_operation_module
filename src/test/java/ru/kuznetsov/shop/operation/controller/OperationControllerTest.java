package ru.kuznetsov.shop.operation.controller;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.kuznetsov.shop.operation.AbstractTest;
import ru.kuznetsov.shop.represent.dto.AddressDto;
import ru.kuznetsov.shop.represent.dto.OperationDto;
import ru.kuznetsov.shop.represent.dto.OperationPayloadDto;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OperationControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void addOperation() throws Exception {
        assertFalse(operationService.containsOperation(operationIdString));

        addNewOperation();

        assertTrue(operationService.containsOperation(operationIdString));
    }

    @Test
    void containsOperationFalse() throws Exception {
        MvcResult mvcResult = sendRequest(HttpMethod.GET, API_PATH + "/" + operationIdString + "/contains", null)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        Boolean result = om.readValue(json, Boolean.class);

        assertFalse(result);
    }

    @Test
    void containsOperationTrue() throws Exception {
        addNewOperation();

        MvcResult mvcResult = sendRequest(HttpMethod.GET, API_PATH + "/" + operationIdString + "/contains", null)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        Boolean result = om.readValue(json, Boolean.class);

        assertTrue(result);
    }

    @Test
    void getOperation() throws Exception {
        addNewOperation();

        MvcResult mvcResult = sendRequest(HttpMethod.GET, API_PATH + "/" + operationIdString, null)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        OperationDto result = om.readValue(json, OperationDto.class);

        assertEquals(operationIdString, result.getId());
        assertEquals(operationType, result.getOperationType());
        assertEquals(operationResult, result.getResult());
    }

    @Test
    void getOperationData() throws Exception {
        addNewOperation();

        MvcResult mvcResult = sendRequest(HttpMethod.GET, API_PATH + "/payload/" + operationIdString, null)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        OperationPayloadDto[] result = om.readValue(json, OperationPayloadDto[].class);

        Optional<Long> first = Arrays.stream(result)
                .map(OperationPayloadDto::getPayloadId)
                .filter(id -> id.equals(testEntityId))
                .findFirst();

        assertTrue(first.isPresent());

        for (OperationPayloadDto payloadDto : result) {
            assertNotNull(payloadDto.getDateTime());
        }
    }

    @Test
    void getOperationDataWait() throws Exception {
        operationService.clearOperations();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<MvcResult> operationData = executorService.submit(() -> {
            try {
                return sendRequest(HttpMethod.GET, API_PATH + "/payload/" + operationIdString + "/wait", null)
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        executorService.submit(() -> {
            try {
                addNewOperation();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        String json = operationData.get().getResponse().getContentAsString();
        Long[] result = om.readValue(json, Long[].class);

        assertTrue(Arrays.asList(result).contains(testEntityId));
    }

    @Test
    void getOperationDataWaitThrows() {
        operationService.clearOperations();

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        Future<MvcResult> operationData = executorService.submit(() -> {
            try {
                return sendRequest(HttpMethod.GET, API_PATH + "/payload/" + operationIdString + "/wait", null)
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertThrows(ExecutionException.class, operationData::get);
    }

    @Test
    void getOperationDataWithObject() throws Exception {
        addNewOperation();

        MvcResult mvcResult = sendRequest(HttpMethod.POST, API_PATH + "/payload", getMockOperationDto())
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        OperationPayloadDto[] result = om.readValue(json, OperationPayloadDto[].class);

        Optional<Long> first = Arrays.stream(result)
                .map(OperationPayloadDto::getPayloadId)
                .filter(id -> id.equals(testEntityId))
                .findFirst();

        assertTrue(first.isPresent());
        assertEquals(testEntityId, first.get());
    }

    @Test
    void deleteOperation() throws Exception {
        MvcResult mvcResult = sendRequest(HttpMethod.GET, API_PATH + "/" + operationIdString + "/contains", null)
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        Boolean result = om.readValue(json, Boolean.class);

        assertFalse(result);

        addNewOperation();

        sendRequest(HttpMethod.DELETE, API_PATH, getMockOperationDto())
                .andDo(print())
                .andExpect(status().isOk());

        mvcResult = sendRequest(HttpMethod.GET, API_PATH + "/" + operationIdString + "/contains", null)
                .andExpect(status().isOk())
                .andReturn();

        json = mvcResult.getResponse().getContentAsString();
        result = om.readValue(json, Boolean.class);

        assertFalse(result);
    }

    @Test
    void deleteOperationBatch() throws Exception {
        MvcResult mvcResult = sendRequest(HttpMethod.GET, API_PATH + "/" + operationIdString + "/contains", null)
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        Boolean result = om.readValue(json, Boolean.class);

        assertFalse(result);

        addNewOperation();

        sendRequest(HttpMethod.DELETE, API_PATH + "/batch", List.of(getMockOperationDto()))
                .andDo(print())
                .andExpect(status().isOk());

        mvcResult = sendRequest(HttpMethod.GET, API_PATH + "/" + operationIdString + "/contains", null)
                .andExpect(status().isOk())
                .andReturn();

        json = mvcResult.getResponse().getContentAsString();
        result = om.readValue(json, Boolean.class);

        assertFalse(result);
    }

    private ResultActions sendRequest(HttpMethod httpMethod, String apiPath, Object body) throws Exception {
        return mockMvc.perform(request(httpMethod, apiPath)
                .content(om.writeValueAsString(body))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
    }

    private void addNewOperation() throws Exception {
        AddressDto mockDto = getMockDto();
        mockDto.setId(testEntityId);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("objectJson", om.writeValueAsString(mockDto));
        requestBody.put("operationId", operationIdString);
        requestBody.put("operationType", operationType);
        requestBody.put("result", operationResult);

        sendRequest(HttpMethod.POST, API_PATH, requestBody)
                .andDo(print())
                .andExpect(status().isOk());
    }
}