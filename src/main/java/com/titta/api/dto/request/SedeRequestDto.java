package com.titta.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
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
    private DireccionRequestDto direccion;

    @Valid
    private List<HorarioRequestSedeDTO> horariosOperacion;


    @Data
    public static class HorarioRequestSedeDTO {

        private Long idHorarioOperacionSede;

        private String diaSemana;

        private LocalTime horaApertura;

        private LocalTime horaCierre;

    }

    @Data
    public static class DireccionRequestDto {

        private String calle;

        private String numeroExterior;

        private String codigoPostal;

        private String ciudad;

        private String estadoProvincial;

    }

}
