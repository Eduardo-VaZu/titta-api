package com.titta.api.features.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@Builder
@JsonPropertyOrder({"message", "status"})
public record AuthRegisterResponse(
        String message,
        boolean status
) {

}
