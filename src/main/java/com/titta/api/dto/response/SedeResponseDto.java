package com.titta.api.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
public class SedeResponseDto {

    private Long idSede;

    private String nombreSede;

    private String telefono;

    private Boolean estado;

    private DireccionResponseDto direccion;

    private List<HorarioSedeResponseDTO> horariosOperacion;

    @Data
    public static class DireccionResponseDto {

        private Long idDireccion;

        private String calle;

        private String numeroExterior;

        private String codigoPostal;

        private String ciudad;

        private String estadoProvincial;

    }

    @Data
    public class HorarioSedeResponseDTO {

        private Long idHorarioOperacionSede;

        private String diaSemana;

        private LocalTime horaApertura;

        private LocalTime horaCierre;

    }


}
