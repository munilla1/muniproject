package com.repository;

import com.model.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByCorreo(String correo);
    long deleteByUsername(String username);
    boolean existsByCorreo(String correo);
    boolean existsByUsername(String username);
    boolean existsByCorreoIgnoreCase(String correo);
    boolean existsByUsernameIgnoreCase(String username);

}
