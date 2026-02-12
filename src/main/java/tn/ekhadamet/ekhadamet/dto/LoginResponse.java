// dto/LoginResponse.java  (keep as is – no change needed)
package tn.ekhadamet.ekhadamet.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;  // will be the email now
    private String role;
}