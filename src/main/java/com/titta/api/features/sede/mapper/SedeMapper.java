package com.titta.api.features.sede;

import com.titta.api.features.sede.dto.SedeRequestDto;
import com.titta.api.features.sede.dto.SedeResponseDto;
import com.titta.api.domain.model.Direccion;
import com.titta.api.domain.model.HorarioOperacionSede;
import com.titta.api.domain.model.Sede;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SedeMapper {

    public Sede toSede(SedeRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Sede sede = Sede.builder()
                .nombreSede(dto.nombreSede())
                .telefono(dto.telefono())
                .estado(dto.estado())
                .direccion(Direccion.builder()
                        .calle(dto.direccion().calle())
                        .numeroExterior(dto.direccion().numeroExterior())
                        .codigoPostal(dto.direccion().codigoPostal())
                        .ciudad(dto.direccion().ciudad())
                        .estadoProvincial(dto.direccion().estadoProvincial())
                        .build())
                .build();

        if (dto.horariosOperacion() != null && !dto.horariosOperacion().isEmpty()) {
            Set<HorarioOperacionSede> horarios = dto.horariosOperacion().stream()
                    .map(horarioDTO -> HorarioOperacionSede.builder()
                            .diaSemana(horarioDTO.diaSemana())
                            .horaApertura(horarioDTO.horaApertura())
                            .horaCierre(horarioDTO.horaCierre())
                            .sede(sede)
                            .build())
                    .collect(Collectors.toSet());
            sede.setHorariosOperacion(horarios);
        }

        return sede;
    }

    public SedeResponseDto toSedeResponseDto(Sede sede) {
        if (sede == null) {
            return null;
        }

        SedeResponseDto.DireccionResponseDto direccionDto = new SedeResponseDto.DireccionResponseDto(
                sede.getDireccion().getIdDireccion(),
                sede.getDireccion().getCalle(),
                sede.getDireccion().getNumeroExterior(),
                sede.getDireccion().getCodigoPostal(),
                sede.getDireccion().getCiudad(),
                sede.getDireccion().getEstadoProvincial()
        );

        List<SedeResponseDto.HorarioSedeResponseDTO> horariosDto = sede.getHorariosOperacion()
                .stream()
                .map(horario -> new SedeResponseDto.HorarioSedeResponseDTO(
                        horario.getIdHorarioOperacionSede(),
                        horario.getDiaSemana(),
                        horario.getHoraApertura(),
                        horario.getHoraCierre()
                ))
                .collect(Collectors.toList());

        return new SedeResponseDto(
                sede.getIdSede(),
                sede.getNombreSede(),
                sede.getTelefono(),
                sede.getEstado(),
                direccionDto,
                horariosDto
        );
    }

}
