package com.titta.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.List;


public record SedeRequestDto(

        @NotBlank(message = "El nombre de la sede es obligatorio.")
        String nombreSede,

        String telefono,

        @NotNull(message = "El estado de la sede es obligatorio.")
        Boolean estado,

        @NotNull(message = "La dirección es obligatoria.")
        @Valid
        DireccionRequestDto direccion,

        @Valid
        List<HorarioRequestSedeDTO> horariosOperacion
) {

    public record HorarioRequestSedeDTO(
            Long idHorarioOperacionSede,
            String diaSemana,
            LocalTime horaApertura,
            LocalTime horaCierre
    ) {

    }

    public record DireccionRequestDto(
            String calle,
            String numeroExterior,
            String codigoPostal,
            String ciudad,
            String estadoProvincial
    ) {

    }

}
