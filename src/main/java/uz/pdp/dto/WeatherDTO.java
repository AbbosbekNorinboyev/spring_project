package uz.pdp.dto;

import java.time.LocalDate;

public record WeatherDTO(Integer city_id, int celsius, int fahrenheit, LocalDate date) {
}
