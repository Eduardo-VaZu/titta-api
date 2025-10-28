package com.titta.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImagenRequestDto {

    @NotBlank(message = "La imagen es obligatoria.")
    private String imagen;

}
