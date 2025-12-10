package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.DTOs.RegistroDTO;
import com.DTOs.EliminarDTO;
import com.DTOs.ModificarDTO;
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
      
      boolean active = TransactionSynchronizationManager.isActualTransactionActive();
      System.out.println("¿Transacción activa? " + active);

      Role rolUsuario = roleRepository.findByName(ERole.USER)
          .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));

      Usuario u = Usuario.builder()
    	        .username(dto.getUsername())
    	        .correo(dto.getCorreo())
    	        .password(passwordEncoder.encode(dto.getPassword()))
    	        .roles(new java.util.HashSet<>(java.util.List.of(rolUsuario))) // mutable
    	        .build();

      return usuarioRepository.save(u); 
    }
    
    @Transactional
    public void modificar(String usernameActual, ModificarDTO dto) {

        Usuario usuario = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Validar username si lo cambia
        if (!usuario.getUsername().equals(dto.getUsername()) &&
            usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe.");
        }

        // Validar correo si lo cambia
        if (!usuario.getCorreo().equals(dto.getCorreo()) &&
            usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        // Validar contraseña actual
        if (!passwordEncoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta.");
        }

        // Validar coincidencia de nueva contraseña
        if (!dto.getPasswordNueva().equals(dto.getPasswordNueva2())) {
            throw new RuntimeException("Las nuevas contraseñas no coinciden.");
        }

        // Validar fortaleza de la nueva contraseña
        if (!isPasswordStrong(dto.getPasswordNueva())) {
            throw new RuntimeException("La nueva contraseña no cumple los requisitos.");
        }

        // Aplicar cambios
        usuario.setUsername(dto.getUsername());
        usuario.setCorreo(dto.getCorreo());
        usuario.setPassword(passwordEncoder.encode(dto.getPasswordNueva()));
    }

    
    public void eliminarUsuario(EliminarDTO dto, String usernameAutenticado) {

        // Verificar que el usuario autenticado está intentando borrarse a sí mismo
        if (!dto.getUsername().equals(usernameAutenticado)) {
            throw new RuntimeException("No puedes eliminar otro usuario.");
        }

        Usuario usuario = usuarioRepository.findByUsername(usernameAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Verificar contraseña
        if (!passwordEncoder.matches(dto.getContrasenaIngresada(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta.");
        }

        usuarioRepository.delete(usuario);
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElse(null);
    }

    private boolean isPasswordStrong(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }
}
