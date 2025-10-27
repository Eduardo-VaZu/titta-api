package com.titta.api.dto.request;

import com.titta.api.model.Direccion;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SedeRequestDto {

    private String nombreSede;

    private String telefono;

    private Boolean estado;

    private Direccion direccion;

    private DireccionRequestDto direccionRequestDto;

    private List<HorarioRequestSedeDTO> horariosOperacion;

}
