package com.titta.api.features.sede;

import com.titta.api.features.sede.dto.SedeRequestDto;
import com.titta.api.features.sede.dto.SedeResponseDto;

import java.util.List;

public interface SedeService {
    SedeResponseDto crearSede(SedeRequestDto sedeDto);
    List<SedeResponseDto> obtenerTodasLasSedes();
}