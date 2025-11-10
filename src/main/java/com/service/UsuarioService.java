package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.DTOs.RegistroDTO;
import com.model.ERole;
import com.model.Role;
import com.model.Usuario;
import com.repository.RoleRepository;
import com.repository.UsuarioRepository;

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
      if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
        throw new RuntimeException("El correo ya está registrado");
      }
      if (usuarioRepository.existsByUsername(dto.getUsername())) {
        throw new RuntimeException("El nombre de usuario ya existe");
      }
      if (!isPasswordStrong(dto.getPassword())) {
        throw new RuntimeException("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial");
      }

      Role rolUsuario = roleRepository.findByName(ERole.USER)
          .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));

      Usuario u = Usuario.builder()
    	        .username(dto.getUsername())
    	        .correo(dto.getCorreo())
    	        .password(passwordEncoder.encode(dto.getPassword()))
    	        .roles(new java.util.HashSet<>(java.util.List.of(rolUsuario))) // mutable
    	        .build();

      return usuarioRepository.saveAndFlush(u); 
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElse(null);
    }

    private boolean isPasswordStrong(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }
}
