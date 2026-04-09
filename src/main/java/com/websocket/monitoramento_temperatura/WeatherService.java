package com.websocket.monitoramento_temperatura;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Locale;
import java.util.Map;

@Service
public class WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();

    public double getTemperature(double lat, double lon) {
        String url = String.format(Locale.US, "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true", lat, lon);
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response != null && response.containsKey("current_weather")) {
            Map<String, Object> currentWeather = (Map<String, Object>) response.get("current_weather");
            return ((Number) currentWeather.get("temperature")).doubleValue();
        }
        return 0.0;
    }

    public String getCondition(double lat, double lon) {
        String url = String.format(Locale.US, "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true", lat, lon);
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response != null && response.containsKey("current_weather")) {
            Map<String, Object> currentWeather = (Map<String, Object>) response.get("current_weather");
            int weatherCode = ((Number) currentWeather.get("weathercode")).intValue();
            return mapWeatherCode(weatherCode);
        }
        return "Desconhecido";
    }

    public Map<String, Object> getCurrentWeather(double lat, double lon) {
        String url = String.format(Locale.US, "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true", lat, lon);
        return restTemplate.getForObject(url, Map.class);
    }

    public String mapWeatherCode(int code) {
        return switch (code) {
            case 0 -> "Céu Limpo";
            case 1, 2, 3 -> "Parcialmente Nublado";
            case 45, 48 -> "Nevoeiro";
            case 51, 53, 55 -> "Garoa";
            case 61, 63, 65 -> "Chuva";
            case 71, 73, 75 -> "Neve";
            case 80, 81, 82 -> "Aguaceiros";
            case 95, 96, 99 -> "Tempestade";
            default -> "Condições Variáveis";
        };
    }
}
