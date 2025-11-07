package com.titta.api.features.sede.service;

import com.titta.api.features.sede.dto.request.SedeRequestDto;
import com.titta.api.features.sede.dto.response.SedeResponseDto;

import java.util.List;

public interface SedeService {
    SedeResponseDto crearSede(SedeRequestDto sedeDto);
    List<SedeResponseDto> obtenerTodasLasSedes();
}