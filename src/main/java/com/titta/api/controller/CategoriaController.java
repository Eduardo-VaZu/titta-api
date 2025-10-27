package com.titta.api.controller;

import com.titta.api.dto.request.CategoriaRequestDto;
import com.titta.api.dto.response.CategoriaResponseDto;
import com.titta.api.model.Categoria;
import com.titta.api.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDto> crearCategoria(@RequestBody CategoriaRequestDto categoriaRequestDto) {
        CategoriaResponseDto nuevaCategoria = categoriaService.crearCategoria(categoriaRequestDto);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDto>> obterCategorias() {
        List<CategoriaResponseDto> categorias = categoriaService.obterCategorias();
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }
}
