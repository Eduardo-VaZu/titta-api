package com.titta.api.features.sede.service.impl;

import com.titta.api.config.exception.DuplicateResourceException;
import com.titta.api.config.exception.ResourceNotFoundException;
import com.titta.api.domain.model.Direccion;
import com.titta.api.domain.model.HorarioOperacionSede;
import com.titta.api.domain.model.Sede;
import com.titta.api.domain.model.enums.EstadoCarritoEnum;
import com.titta.api.domain.repository.CartRepository;
import com.titta.api.domain.repository.SedeRepository;
import com.titta.api.features.sede.dto.request.SedeRequestDto;
import com.titta.api.features.sede.dto.response.SedeResponseDto;
import com.titta.api.features.sede.mapper.SedeMapper;
import com.titta.api.features.sede.service.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SedeServiceImpl implements SedeService {

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private SedeMapper sedeMapper;

    @Autowired
    private CartRepository cartRepository;

    @Override
    @Transactional
    public SedeResponseDto crearSede(SedeRequestDto sedeDto) {
        if (sedeRepository.existsByNombreSede(sedeDto.nombreSede())) {
            throw new DuplicateResourceException("Ya existe una sede con el nombre '" + sedeDto.nombreSede() + "'.");
        }

        if (sedeDto.telefono() != null && sedeRepository.existsByTelefono(sedeDto.telefono())) {
            throw new DuplicateResourceException("El teléfono '" + sedeDto.telefono() + "' ya está registrado en otra sede.");
        }

        Sede sede = sedeMapper.toSede(sedeDto);
        Sede nuevaSede = sedeRepository.save(sede);

        return sedeMapper.toSedeResponseDto(nuevaSede);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SedeResponseDto> obtenerTodasLasSedes(Boolean estado, Pageable pageable) {
        Page<Sede> sedesPage;
        if (estado != null) {
            sedesPage = sedeRepository.findByEstado(estado, pageable);
        } else {
            sedesPage = sedeRepository.findAll(pageable);
        }
        return sedesPage.map(sedeMapper::toSedeResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public SedeResponseDto getSedeById(Long idSede) {
        Sede sede = sedeRepository.findById(idSede)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada con ID: " + idSede));
        return sedeMapper.toSedeResponseDto(sede);
    }

    @Override
    @Transactional
    public SedeResponseDto updateSede(Long idSede, SedeRequestDto sedeDto) {
        Sede sede = sedeRepository.findById(idSede)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada con ID: " + idSede));

        if (!sede.getNombreSede().equals(sedeDto.nombreSede())) {
            Optional<Sede> otraSedeConMismoNombre = sedeRepository.findByNombreSede(sedeDto.nombreSede());
            if (otraSedeConMismoNombre.isPresent()) {
                throw new DuplicateResourceException("El nombre '" + sedeDto.nombreSede() + "' ya está en uso por otra sede.");
            }
        }

        sede.setNombreSede(sedeDto.nombreSede());
        sede.setTelefono(sedeDto.telefono());
        sede.setEstado(sedeDto.estado());

        Direccion direccion = sede.getDireccion();
        SedeRequestDto.DireccionRequestDto dirDto = sedeDto.direccion();
        direccion.setCalle(dirDto.calle());
        direccion.setNumeroExterior(dirDto.numeroExterior());
        direccion.setCodigoPostal(dirDto.codigoPostal());
        direccion.setCiudad(dirDto.ciudad());
        direccion.setEstadoProvincial(dirDto.estadoProvincial());

        sede.getHorariosOperacion().clear();
        if (sedeDto.horariosOperacion() != null && !sedeDto.horariosOperacion().isEmpty()) {
            Set<HorarioOperacionSede> nuevosHorarios = sedeDto.horariosOperacion().stream()
                    .map(horarioDTO -> HorarioOperacionSede.builder()
                            .diaSemana(horarioDTO.diaSemana())
                            .horaApertura(horarioDTO.horaApertura())
                            .horaCierre(horarioDTO.horaCierre())
                            .sede(sede) // <- Importante: vincular al padre
                            .build())
                    .collect(Collectors.toSet());
            sede.getHorariosOperacion().addAll(nuevosHorarios);
        }

        Sede sedeActualizada = sedeRepository.save(sede);
        return sedeMapper.toSedeResponseDto(sedeActualizada);
    }

    @Override
    @Transactional
    public void deleteSede(Long idSede) {

        Sede sede = sedeRepository.findById(idSede)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada con ID: " + idSede));

        if (cartRepository.existsBySede_IdSedeAndEstado(idSede, EstadoCarritoEnum.ACTIVO)) {
            throw new DataIntegrityViolationException(
                    "No se puede desactivar la sede (ID: " + idSede + "). Aún tiene carritos activos asociados."
            );
        }

        sede.setEstado(false);
        sedeRepository.save(sede);
    }
}