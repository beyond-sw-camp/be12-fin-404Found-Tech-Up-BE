package com.example.backend.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignupRequest {
    private String nickname;
    private String email;
    private String validatedString;
    private String password;
    private Boolean agreement;
}
