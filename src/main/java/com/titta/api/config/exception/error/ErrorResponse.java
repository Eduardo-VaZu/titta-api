package com.titta.api.exception.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo para representar una respuesta de error estándar")
public class ErrorResponse {

    @Schema(description = "Código de estado HTTP.", example = "404")
    private int statusCode;

    @Schema(description = "Mensaje detallado del error.", example = "El recurso solicitado no fue encontrado.")
    private String message;

    @Schema(description = "Fecha y hora en que ocurrió el error.", example = "2025-10-27T15:30:00")
    private LocalDateTime timestamp;
}
