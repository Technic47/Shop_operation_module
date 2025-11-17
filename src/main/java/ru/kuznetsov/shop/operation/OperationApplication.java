package ru.kuznetsov.shop.operation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.kuznetsov.shop.parameter.config.ParameterConfig;

@SpringBootApplication
@Import(ParameterConfig.class)
public class OperationApplication {

	public static void main(String[] args) {
		SpringApplication.run(OperationApplication.class, args);
	}

}
