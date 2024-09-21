package uz.pdp.domain;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AuthUser {
    private Long id;
    private String username;
    private String password;
    private boolean active;
    private String role;
}
