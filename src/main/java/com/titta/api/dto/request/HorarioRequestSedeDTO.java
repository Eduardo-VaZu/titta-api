package com.titta.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
public class HorarioRequestSedeDTO {

    private Long idHorarioOperacionSede;

    private String diaSemana;

    private LocalTime horaApertura;

    private LocalTime horaCierre;

}