package uz.pdp.domain;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SubscribedCity {
    private Integer id;
    private int user_id;
    private String city_name;
    private int city_id;
}
