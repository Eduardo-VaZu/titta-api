package com.titta.api.mapper;

import com.titta.api.dto.request.SedeRequestDto;
import com.titta.api.dto.response.DireccionResponseDto;
import com.titta.api.dto.response.HorarioSedeResponseDTO;
import com.titta.api.dto.response.SedeResponseDto;
import com.titta.api.model.Direccion;
import com.titta.api.model.HorarioOperacionSede;
import com.titta.api.model.Sede;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SedeMapper {

    public Sede toSede(SedeRequestDto dto){
        if(dto == null){
            return null;
        }
        Sede sede = new Sede();
        sede.setNombreSede(dto.getNombreSede());
        sede.setTelefono(dto.getTelefono());
        sede.setEstado(dto.getEstado());

        Direccion direccion = new Direccion();
        direccion.setCalle(dto.getDireccion().getCalle());
        direccion.setNumeroExterior(dto.getDireccion().getNumeroExterior());
        direccion.setCodigoPostal(dto.getDireccion().getCodigoPostal());
        direccion.setCiudad(dto.getDireccion().getCiudad());
        direccion.setEstadoProvincial(dto.getDireccion().getEstadoProvincial());
        sede.setDireccion(direccion);

        if(dto.getHorariosOperacion() != null && !dto.getHorariosOperacion().isEmpty()){
            dto.getHorariosOperacion().forEach(horario -> {
                HorarioOperacionSede horarioOperacionSede = new HorarioOperacionSede();
                horarioOperacionSede.setDiaSemana(horario.getDiaSemana());
                horarioOperacionSede.setHoraApertura(horario.getHoraApertura());
                horarioOperacionSede.setHoraCierre(horario.getHoraCierre());
                horarioOperacionSede.setSede(sede);
                sede.getHorariosOperacion().add(horarioOperacionSede);
            });
        }

        return sede;
    }

    public SedeResponseDto toSedeResponseDto(Sede sede){
        if(sede == null){
            return null;
        }
        SedeResponseDto dto = new SedeResponseDto();
        dto.setIdSede(sede.getIdSede());
        dto.setNombreSede(sede.getNombreSede());
        dto.setTelefono(sede.getTelefono());
        dto.setEstado(sede.getEstado());
        dto.setDireccionRequestDto(toDireccionResponseDto(sede.getDireccion()));
        dto.setHorariosOperacion(sede.getHorariosOperacion()
                .stream()
                .map(this::toHorarioResponseDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public DireccionResponseDto toDireccionResponseDto(Direccion direccion){
        if(direccion == null){
            return null;
        }
        DireccionResponseDto dto = new DireccionResponseDto();
        dto.setIdDireccion(direccion.getIdDireccion());
        dto.setCalle(direccion.getCalle());
        dto.setNumeroExterior(direccion.getNumeroExterior());
        dto.setCodigoPostal(direccion.getCodigoPostal());
        dto.setCiudad(direccion.getCiudad());
        dto.setEstadoProvincial(direccion.getEstadoProvincial());
        return dto;
    }

    public HorarioSedeResponseDTO toHorarioResponseDTO(HorarioOperacionSede horario) {
        if (horario == null) {
            return null;
        }
        HorarioSedeResponseDTO dto = new HorarioSedeResponseDTO();
        dto.setIdHorarioOperacionSede(horario.getIdHorarioOperacionSede());
        dto.setDiaSemana(horario.getDiaSemana());
        dto.setHoraApertura(horario.getHoraApertura());
        dto.setHoraCierre(horario.getHoraCierre());
        return dto;
    }
}
