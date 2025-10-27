package com.titta.api.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SedeResponseDto {

    private Long idSede;

    private String nombreSede;

    private String telefono;

    private Boolean estado;

    private DireccionResponseDto direccionRequestDto;

    private List<HorarioSedeResponseDTO> horariosOperacion;

}
