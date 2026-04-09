package com.websocket.monitoramento_temperatura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonitoramentoTemperaturaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitoramentoTemperaturaApplication.class, args);
	}

}
