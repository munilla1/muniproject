package com.service;

import com.DTOs.RegistroDTO;
import com.model.ERole;
import com.model.Role;
import com.model.Usuario;
import com.repository.RoleRepository;
import com.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrar(RegistroDTO dto) {
        // Normaliza entrada
        final String correo    = dto.getCorreo()   == null ? "" : dto.getCorreo().trim().toLowerCase();
        final String username  = dto.getUsername() == null ? "" : dto.getUsername().trim();
        final String rawPass   = dto.getPassword() == null ? "" : dto.getPassword();

        // Validaciones básicas
        if (correo.isBlank() || username.isBlank() || rawPass.isBlank()) {
            throw new IllegalArgumentException("Correo, usuario y contraseña son obligatorios.");
        }
        if (!isPasswordStrong(rawPass)) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial."
            );
        }

        // Comprobaciones de existencia (mejor en ignore-case)
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new IllegalStateException("El correo ya está registrado.");
        }
        if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalStateException("El nombre de usuario ya existe.");
        }

        // Obtiene el rol USER (coincidiendo con lo que tengas en DB)
        // ERole.USER.name() -> "USER"
        Optional<Role> rolOpt = roleRepository.findByName(ERole.USER.name());
        // Si tienes un método ignore case, usa: roleRepository.findByNameIgnoreCase(ERole.USER.name())
        Role rolUsuario = rolOpt.orElseThrow(() -> new IllegalStateException("Rol USER no encontrado"));

        // Construye el usuario
        Set<Role> roles = new HashSet<>();
        roles.add(rolUsuario);

        Usuario u = Usuario.builder()
                .username(username)
                .correo(correo)
                .password(passwordEncoder.encode(rawPass))
                .roles(roles)
                .build();

        // Guarda (saveAndFlush si quieres ver el INSERT inmediatamente en logs)
        return usuarioRepository.save(u);
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username).orElse(null);
    }

    private boolean isPasswordStrong(String password) {
        return password != null &&
               password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }
}

