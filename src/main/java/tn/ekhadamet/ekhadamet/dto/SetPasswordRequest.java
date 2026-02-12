// dto/SetPasswordRequest.java  (optional – keep if you want to allow password change later)
package tn.ekhadamet.ekhadamet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SetPasswordRequest {

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String confirmPassword;  // optional – validate equality in controller if needed
}