package com.titta.api.features.sede.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.List;


public record SedeRequestDto(

        @NotBlank(message = "El nombre de la sede es obligatorio.")
        @Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
        String nombreSede,

        @Size(max = 20, message = "El teléfono no debe exceder los 20 caracteres")
        // Opcional: un patrón si quieres validar el formato del teléfono
        // @Pattern(regexp = "^[+]*[0-9]{9,15}$", message = "Formato de teléfono no válido")
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

            @NotBlank(message = "El día de la semana es obligatorio")
            @Size(max = 15, message = "El día de la semana no debe exceder los 15 caracteres")
            String diaSemana,

            LocalTime horaApertura,

            LocalTime horaCierre
    ) {

    }

    public record DireccionRequestDto(
            @Size(max = 255, message = "La calle no debe exceder los 255 caracteres")
            String calle,

            @Size(max = 20, message = "El número exterior no debe exceder los 20 caracteres")
            String numeroExterior,

            @Size(max = 20, message = "El código postal no debe exceder los 20 caracteres")
            String codigoPostal,

            @NotBlank(message = "La ciudad es obligatoria")
            @Size(max = 255, message = "La ciudad no debe exceder los 255 caracteres")
            String ciudad,

            @NotBlank(message = "El estado/provincia es obligatorio")
            @Size(max = 255, message = "El estado/provincia no debe exceder los 255 caracteres")
            String estadoProvincial
    ) {

    }

}
