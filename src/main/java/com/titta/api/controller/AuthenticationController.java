// Ubicación: com/titta/api/controller/AuthenticationController.java

package com.titta.api.controller;

import com.titta.api.dto.auth.AuthLoginRequest;
import com.titta.api.dto.auth.AuthRegisterRequest;
import com.titta.api.dto.auth.AuthResponse;
import com.titta.api.service.auth.UserDetailServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthLoginRequest userRequest) {
        AuthResponse response = this.userDetailService.loginUser(userRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthRegisterRequest registerRequest) {
        AuthResponse response = userDetailService.registerUser(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED); // Devolvemos un estado 201 Created.
    }
}
