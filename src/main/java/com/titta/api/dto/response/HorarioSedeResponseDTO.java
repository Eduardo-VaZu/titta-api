package com.titta.api.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
public class HorarioSedeResponseDTO {

    private Long idHorarioOperacionSede;

    private String diaSemana;

    private LocalTime horaApertura;

    private LocalTime horaCierre;

}