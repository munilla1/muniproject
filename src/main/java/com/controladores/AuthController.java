package com.controladores;

import com.DTOs.RegistroDTO;
import com.model.Usuario;
import com.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class AuthController {

    private final UsuarioService usuarioService;

    @Autowired
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/registro-login";
    }

    @GetMapping("/registro-login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            Model model,
            HttpSession session) {

        model.addAttribute("registroDTO", new RegistroDTO());

        // Nombre de usuario en sesión
        Object usuarioNombre = session.getAttribute("usuarioNombre");
        model.addAttribute("usuarioNombre", usuarioNombre);

        // Error de acceso
        if (error != null) {
            model.addAttribute("errorDeAcceso", "Usuario o contraseña incorrectos");
        }

        return "registro-login";
    }


    @PostMapping("/guardar")
    public String registerUser(@ModelAttribute RegistroDTO registroDTO, Model model) {
        try {
            usuarioService.registrar(registroDTO);
            model.addAttribute("mensajeRegistro", "Usuario registrado exitosamente.");
            model.addAttribute("registroDTO", new RegistroDTO()); // Limpiar formulario
            return "registro-login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registroDTO", registroDTO);
            return "registro-login";
        }
    }
    
    @GetMapping("/pagPrincipalJuego")
    public String pagPrincipalJuego(HttpSession session, Model model) {
        Object usuarioNombre = session.getAttribute("usuarioNombre");
        model.addAttribute("usuarioNombre", usuarioNombre);
        return "pagPrincipalJuego";
    }
    
    @PostMapping("/login-success")
    public String loginSuccess(HttpSession session, Principal principal) {
        session.setAttribute("usuarioNombre", principal.getName());
        return "redirect:/pagPrincipalJuego";
    }


}
