package ru.pavelnazaro.userrequests.payload;

import lombok.Data;

@Data
public class JwtAuthenticationResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private String message;

    public JwtAuthenticationResponse(String accessToken, String message) {
        this.accessToken = accessToken;
        this.message = message;
    }

}
