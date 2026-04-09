package com.websocket.monitoramento_temperatura;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class WeatherScheduler {

    private final SimpMessagingTemplate messagingTemplate;
    private final WeatherService weatherService;
    private final Random random = new Random();

    private final List<CityConfig> cities = List.of(
            new CityConfig("Vianópolis", -16.74, -48.51),
            new CityConfig("São Paulo", -23.55, -46.63),
            new CityConfig("Rio de Janeiro", -22.90, -43.17),
            new CityConfig("Curitiba", -25.42, -49.27),
            new CityConfig("Porto Alegre", -30.03, -51.23),
            new CityConfig("Belo Horizonte", -19.92, -43.94),
            new CityConfig("Brasília", -15.78, -47.93),
            new CityConfig("Fortaleza", -3.71, -38.54),
            new CityConfig("Manaus", -3.11, -60.02),
            new CityConfig("Salvador", -12.97, -38.50)
    );

    public WeatherScheduler(SimpMessagingTemplate messagingTemplate, WeatherService weatherService) {
        this.messagingTemplate = messagingTemplate;
        this.weatherService = weatherService;
    }

    @Scheduled(fixedRate = 5000)
    public void enviarClima() {
        CityConfig city = cities.get(random.nextInt(cities.size()));
        
        try {
            Map<String, Object> weatherResponse = weatherService.getCurrentWeather(city.lat, city.lon);
            if (weatherResponse != null && weatherResponse.containsKey("current_weather")) {
                Map<String, Object> current = (Map<String, Object>) weatherResponse.get("current_weather");
                
                double temp = ((Number) current.get("temperature")).doubleValue();
                int code = ((Number) current.get("weathercode")).intValue();
                String condition = weatherService.mapWeatherCode(code);
                String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                WeatherData data = new WeatherData(city.name, temp, condition, time);
                System.out.println("Broadcasting weather data: " + city.name + " (" + temp + "°C)");
                messagingTemplate.convertAndSend("/topic/clima", data);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar clima: " + e.getMessage());
        }
    }

    private static class CityConfig {
        String name;
        double lat;
        double lon;

        CityConfig(String name, double lat, double lon) {
            this.name = name;
            this.lat = lat;
            this.lon = lon;
        }
    }
}
