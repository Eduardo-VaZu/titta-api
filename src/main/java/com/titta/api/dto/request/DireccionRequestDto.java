package com.titta.api.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DireccionRequestDto {

    private String calle;

    private String numeroExterior;

    private String codigoPostal;

    private String ciudad;

    private String estadoProvincial;

}
