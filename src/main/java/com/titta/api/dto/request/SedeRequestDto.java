package com.titta.api.dto.request;

import com.titta.api.model.Direccion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SedeRequestDto {

    @NotBlank(message = "El nombre de la sede es obligatorio.")
    private String nombreSede;

    private String telefono;

    @NotNull(message = "El estado de la sede es obligatorio.")
    private Boolean estado;

    @NotNull(message = "La dirección es obligatoria.")
    @Valid
    private DireccionRequestDto direccionRequestDto;

    @Valid
    private List<HorarioRequestSedeDTO> horariosOperacion;

}
