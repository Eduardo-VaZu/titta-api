package com.titta.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoriaRequestDto {

    @NotBlank(message = "El nombre de la categoría no puede ser nulo o estar vacío.")
    private String nombreCategoria;
}
