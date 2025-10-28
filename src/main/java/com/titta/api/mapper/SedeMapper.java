package com.titta.api.mapper;

import com.titta.api.dto.request.SedeRequestDto;
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

    public SedeResponseDto toSedeResponseDto(Sede sede) {
        if (sede == null) {
            return null;
        }
        SedeResponseDto dto = new SedeResponseDto();
        dto.setIdSede(sede.getIdSede());
        dto.setNombreSede(sede.getNombreSede());
        dto.setTelefono(sede.getTelefono());
        dto.setEstado(sede.getEstado());

        SedeResponseDto.DireccionResponseDto direccionDto = new SedeResponseDto.DireccionResponseDto();
        direccionDto.setIdDireccion(sede.getDireccion().getIdDireccion());
        direccionDto.setCalle(sede.getDireccion().getCalle());
        direccionDto.setNumeroExterior(sede.getDireccion().getNumeroExterior());
        direccionDto.setCodigoPostal(sede.getDireccion().getCodigoPostal());
        direccionDto.setCiudad(sede.getDireccion().getCiudad());
        direccionDto.setEstadoProvincial(sede.getDireccion().getEstadoProvincial());
        dto.setDireccion(direccionDto);
        
        dto.setHorariosOperacion(sede.getHorariosOperacion()
                .stream()
                .map(horarioDto -> {
                    SedeResponseDto.HorarioSedeResponseDTO horarioSedeDto = new SedeResponseDto.HorarioSedeResponseDTO();
                    horarioSedeDto.setIdHorarioOperacionSede(horarioDto.getIdHorarioOperacionSede());
                    horarioSedeDto.setDiaSemana(horarioDto.getDiaSemana());
                    horarioSedeDto.setHoraApertura(horarioDto.getHoraApertura());
                    horarioSedeDto.setHoraCierre(horarioDto.getHoraCierre());
                    return horarioSedeDto;
                })
                .collect(Collectors.toList()));
    
        return dto;
    }

}
