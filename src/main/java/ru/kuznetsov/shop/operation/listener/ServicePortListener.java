package ru.kuznetsov.shop.operation.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.kuznetsov.shop.parameter.service.ParameterService;

import static ru.kuznetsov.shop.parameter.common.ParameterKey.OPERATION_PORT_PARAMETER;

@Service
@RequiredArgsConstructor
public class ServicePortListener {

    private final ParameterService parameterService;

    @EventListener
    public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
        parameterService.update(OPERATION_PORT_PARAMETER, String.valueOf(event.getWebServer().getPort()));
    }
}
