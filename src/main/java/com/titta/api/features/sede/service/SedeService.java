package com.titta.api.features.sede.service;

import com.titta.api.features.sede.dto.request.SedeRequestDto;
import com.titta.api.features.sede.dto.response.SedeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SedeService {

    SedeResponseDto crearSede(SedeRequestDto sedeDto);

    Page<SedeResponseDto> obtenerTodasLasSedes(Boolean estado, Pageable pageable);

    SedeResponseDto getSedeById(Long idSede);

    SedeResponseDto updateSede(Long idSede, SedeRequestDto sedeDto);

    void deleteSede(Long idSede);
}