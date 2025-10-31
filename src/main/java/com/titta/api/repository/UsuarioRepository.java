package com.titta.api.repository;

import com.titta.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Este método buscará un usuario por su email. Spring Data JPA lo implementará automáticamente.
    Optional<Usuario> findByEmail(String email);
}
