package tn.ekhadamet.ekhadamet.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class OtpLoginRequest {
    @NotBlank @Email
    private String email;

    @NotBlank
    private String otp;
}