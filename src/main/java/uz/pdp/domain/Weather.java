package uz.pdp.domain;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Weather {
    private Integer id;
    private int city_id;
    private int celsius;
    private int fahrenheit;
    private LocalDate dates;
}
