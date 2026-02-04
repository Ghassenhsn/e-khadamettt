// src/main/java/tn/ekhadamet/ekhadamet/dto/LoginRequestDTO.java
package tn.ekhadamet.ekhadamet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LoginRequest {
    @NotBlank
    private String phone; // or email
    @NotBlank
    private String password;
}