package com.titta.api.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DireccionResponseDto {

    private Long idDireccion;

    private String calle;

    private String numeroExterior;

    private String codigoPostal;

    private String ciudad;

    private String estadoProvincial;

}
