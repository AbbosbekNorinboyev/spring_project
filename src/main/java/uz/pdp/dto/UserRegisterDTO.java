package uz.pdp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Getter
@Setter
public class UserRegisterDTO {
    @NotBlank(message = "Username Cannot be Blank")
    String username;

    @NotBlank(message = "Password Cannot be Blank")
    String password;

    String role;

    //    @NotBlank(message = "Password Cannot be Blank")
    MultipartFile file;
}
