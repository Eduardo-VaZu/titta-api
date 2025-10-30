package com.titta.api.service.impl;

import com.titta.api.dto.request.SedeRequestDto;
import com.titta.api.dto.response.SedeResponseDto;
import com.titta.api.exception.DuplicateResourceException;
import com.titta.api.mapper.SedeMapper;
import com.titta.api.model.Sede;
import com.titta.api.repository.SedeRepository;
import com.titta.api.service.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SedeServiceImpl implements SedeService {

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private SedeMapper sedeMapper;

    @Override
    @Transactional
    public SedeResponseDto crearSede(SedeRequestDto sedeDto) {
        if (sedeRepository.existsByNombreSede(sedeDto.nombreSede())) {
            throw new DuplicateResourceException("Ya existe una sede con el nombre '" + sedeDto.nombreSede() + "'.");
        }

        Sede sede = sedeMapper.toSede(sedeDto);
        Sede nuevaSede = sedeRepository.save(sede);

        return sedeMapper.toSedeResponseDto(nuevaSede);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SedeResponseDto> obtenerTodasLasSedes() {
        return sedeRepository.findAll()
                .stream()
                .map(sedeMapper::toSedeResponseDto)
                .collect(Collectors.toList());
    }
}