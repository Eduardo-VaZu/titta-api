package com.titta.api.dto.auth;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// Con @JsonPropertyOrder, controlamos el orden en que los campos aparecen en la respuesta JSON.
@JsonPropertyOrder({"username", "message", "jwt", "status"})
public record AuthResponse(
        String username,
        String message,
        String jwt,
        boolean status
) {
}
