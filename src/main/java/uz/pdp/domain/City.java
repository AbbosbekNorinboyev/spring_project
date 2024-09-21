package uz.pdp.domain;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class City {
    private Integer id;
    private String name;
    private String country;
    private String created_by;
}
