package com.titta.api.dto.response;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

public record SedeResponseDto(
        Long idSede,
        String nombreSede,
        String telefono,
        Boolean estado,
        DireccionResponseDto direccion,
        List<HorarioSedeResponseDTO> horariosOperacion
) {

    public record DireccionResponseDto(
            Long idDireccion,
            String calle,
            String numeroExterior,
            String codigoPostal,
            String ciudad,
            String estadoProvincial
    ) {

    }

    public record HorarioSedeResponseDTO(
            Long idHorarioOperacionSede,
            String diaSemana,
            LocalTime horaApertura,
            LocalTime horaCierre
    ) {

    }

}
