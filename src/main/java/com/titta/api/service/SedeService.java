package com.titta.api.service;

import com.titta.api.dto.request.SedeRequestDto;
import com.titta.api.dto.response.SedeResponseDto;

import java.util.List;

public interface SedeService {
    SedeResponseDto crearSede(SedeRequestDto sedeDto);
    List<SedeResponseDto> obtenerTodasLasSedes();
}