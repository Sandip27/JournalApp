package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.api.response.WeatherResponse;
import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.constants.Placeholders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    @Value("${weather.api.key}")
    private String apiKey;
    @Autowired
    private AppCache appCache;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisService redisService;

    public WeatherResponse getWeather(String city) {
        WeatherResponse cachedWeatherResponse = redisService.getData("weather_of_" + city, WeatherResponse.class);

        if (cachedWeatherResponse != null) {
            return cachedWeatherResponse;
        }
        else {
            String finalAPI = appCache.appCache.get(AppCache.keys.WEATHER_API.toString())
                    .replace(Placeholders.CITY, city)
                    .replace(Placeholders.API_KEY, apiKey);

            ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalAPI, HttpMethod.GET, null, WeatherResponse.class);
            WeatherResponse weatherResponse = response.getBody();

            if (weatherResponse != null) {
                redisService.setData("weather_of_" + city, weatherResponse, 300l);
            }
            return weatherResponse;
        }
    }

    // Sample post method using RestTemplate
   /* public WeatherResponse postWeather(String city) {
        String finalAPI = API.replace("CITY", city).replace("API_KEY", apiKey);

        User user = User.builder().username("test").password("test").build();
        HttpEntity<User> httpEntity = new HttpEntity<>(user);

        ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalAPI, HttpMethod.POST, httpEntity, WeatherResponse.class);

        WeatherResponse weatherResponse = response.getBody();
        return weatherResponse;
    }*/
}
