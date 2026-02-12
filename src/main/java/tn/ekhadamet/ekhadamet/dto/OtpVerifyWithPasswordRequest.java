package tn.ekhadamet.ekhadamet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// OtpVerifyWithPasswordRequest.java
public class OtpVerifyWithPasswordRequest {
    private String phone;
    private String code;
    private String password;   // ← NEW: user sets password here

    // getters & setters
}