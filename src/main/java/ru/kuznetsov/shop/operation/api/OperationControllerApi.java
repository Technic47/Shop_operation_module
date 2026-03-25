package ru.kuznetsov.shop.operation.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.kuznetsov.shop.operation.dto.OperationRequest;
import ru.kuznetsov.shop.represent.dto.OperationDto;
import ru.kuznetsov.shop.represent.dto.OperationPayloadDto;

import java.util.List;

public interface OperationControllerApi {

    @Operation(summary = "Поиск операции по id", description = "Присутствует ли операция в сервисе")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = Boolean.class)
                    ),
                    description = "Присутствует ли операция"
            )
    })
    ResponseEntity<Boolean> containsOperation(
            @Parameter(description = "Уникальный идентификатор операции для поиска",
                    schema = @Schema(
                            description = "Id операции (uuid)",
                            example = "95381fbe-b068-4e88-abf5-85e96f64f507"
                    )
            )
            @PathVariable String id);

    @Operation(summary = "Поиск операции по id", description = "Получение операции по id записи")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OperationDto.class)
                    ),
                    description = "Операция"
            ),
            @ApiResponse(responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Операция не найдена")
    })
    ResponseEntity<OperationDto> getOperation(
            @Parameter(description = "Уникальный идентификатор операции для поиска",
                    schema = @Schema(
                            description = "Id операции (uuid)",
                            example = "95381fbe-b068-4e88-abf5-85e96f64f507"
                    )
            )
            @PathVariable String id);

    @Operation(summary = "Поиск объекта операции по id операции", description = "Получение объекта операции по id записи")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OperationPayloadDto[].class)
                    ),
                    description = "Объект операции"
            ),
            @ApiResponse(responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Объект операции не найден")
    })
    ResponseEntity<List<OperationPayloadDto>> getOperationData(
            @Parameter(description = "Уникальный идентификатор операции для поиска",
                    schema = @Schema(
                            description = "Id операции (uuid)",
                            example = "95381fbe-b068-4e88-abf5-85e96f64f507"
                    )
            )
            @PathVariable String id);

    @Operation(summary = "Поиск объекта операции по id с ожиданием", description = "Получение объекта сущности после регистрации операции. Может занимать время.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Long[].class)
                    ),
                    description = "id сущностей"
            ),
            @ApiResponse(responseCode = "404",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Ошибка обработки операции")
    })
    ResponseEntity<List<Long>> getOperationDataWait(
            @Parameter(description = "Уникальный идентификатор операции для поиска",
                    schema = @Schema(
                            description = "Id операции (uuid)",
                            example = "95381fbe-b068-4e88-abf5-85e96f64f507"
                    )
            )
            @PathVariable String id);

    @Operation(summary = "Добавление операции", description = "Добавление операции")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция добавлена"),
            @ApiResponse(responseCode = "404", description = "Ошибка добаления операции")
    })
    void addOperation(
            @Parameter(description = "Модель запроса на добавление операции", required = true,
                    schema = @Schema(
                            implementation = OperationRequest.class,
                            description = "Запрос операции"
                    ))
            @RequestBody OperationRequest operationRequest);

    @Operation(summary = "Поиск объекта операции по модели операции", description = "Получение объекта операции по модели операции")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OperationPayloadDto[].class)
                    ),
                    description = "Объект операции"
            ),
            @ApiResponse(responseCode = "204",
                    content = @Content(
                            schema = @Schema(hidden = true)
                    ),
                    description = "Объект операции не найден")
    })
    ResponseEntity<List<OperationPayloadDto>> getOperationData(
            @Parameter(description = "Модель операции для поиска", required = true,
                    schema = @Schema(
                            implementation = OperationDto.class,
                            description = "Модель операции"
                    ))
            @RequestBody OperationDto operation);

    @Operation(summary = "Удаление нескольких операций по моделям", description = "Удаление нескольких операций по моделям")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операции удалены"),
            @ApiResponse(responseCode = "404", description = "Операции не найдены")
    })
    void deleteOperationBatch(
            @Parameter(description = "Модель операции для удаления", required = true,
                    schema = @Schema(
                            implementation = OperationDto[].class,
                            description = "Модель операции"
                    ))
            @RequestBody List<OperationDto> operationDtoList);

    @Operation(summary = "Удаление операции по моделе", description = "Удаление операции по моделе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция удалена"),
            @ApiResponse(responseCode = "404", description = "Операции не найдена")
    })
    void deleteOperation(
            @Parameter(description = "Модель операции для удаления", required = true,
                    schema = @Schema(
                            implementation = OperationDto.class,
                            description = "Модель операции"
                    ))
            @RequestBody OperationDto operationDto);
}
