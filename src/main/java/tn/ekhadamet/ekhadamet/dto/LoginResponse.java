package tn.ekhadamet.ekhadamet.dto;

public class LoginResponse {

    private String token;
    private Long userId;       // citizen's ID
    private String username;   // phone number (or CIN – depending on what you set as JWT subject)
    private String role;

    // Default constructor
    public LoginResponse() {
    }

    // Optional: constructor for easier creation
    public LoginResponse(String token, Long userId, String username, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This field represents the identifier used for login / JWT subject.
     * Currently: phone number (as per your latest requirement).
     * If you switch back to CIN, you can rename this field to "cin" later.
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}