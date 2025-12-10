package com.controladores;

import com.DTOs.RegistroDTO;
import com.DTOs.EliminarDTO;
import com.DTOs.ModificarDTO;
import com.model.Usuario;
import com.service.CustomUserDetails;
import com.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    
    @GetMapping("/registro")
    public String registrarse(Model model) {

        // Esto es indispensable para que el formulario no cause error 500.
        model.addAttribute("registroDTO", new RegistroDTO());

        return "registro";  // página pública
    }
    
    @GetMapping("/infografias")
    public String verInfografias(HttpSession session, Model model) {
    	Object usuarioNombre = session.getAttribute("usuarioNombre");
        model.addAttribute("usuarioNombre", usuarioNombre);
        return "infografias";
    }

    @PostMapping("/guardar")
    public String registerUser(@ModelAttribute RegistroDTO registroDTO, RedirectAttributes redirectAttributes, Model model) {
        try {
            usuarioService.registrar(registroDTO);
            redirectAttributes.addFlashAttribute("mensajeRegistro", "Usuario registrado exitosamente.");
            model.addAttribute("registroDTO", new RegistroDTO()); // Limpiar formulario
            return "redirect:/registro-login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registroDTO", registroDTO);
            return "registro";
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
	 
	
	  @PostMapping("/modificar")
	  public String modificar(@ModelAttribute ModificarDTO modificarDTO,
	                          @AuthenticationPrincipal CustomUserDetails userDetails,
	                          RedirectAttributes redirectAttributes,
	                          HttpSession session) {
	
	      try {
	          usuarioService.modificar(userDetails.getUsername(), modificarDTO);
	
	          session.invalidate();
	
	          redirectAttributes.addFlashAttribute("mensaje",
	              "Perfil actualizado correctamente. Por seguridad, inicia sesión de nuevo.");
	
	          return "redirect:/registro-login";
	
	      } catch (RuntimeException e) {
	
	          redirectAttributes.addFlashAttribute("errorModificar", e.getMessage());
	          return "redirect:/perfil";
	      }
	  }

    
    @PostMapping("/eliminar")
    public String eliminar(@ModelAttribute EliminarDTO eliminarDTO,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           RedirectAttributes redirectAttributes,
                           HttpSession session) {

        try {
            usuarioService.eliminarUsuario(eliminarDTO, userDetails.getUsername());

            session.invalidate();

            redirectAttributes.addFlashAttribute("mensajeEliminacion",
                    "Tu cuenta ha sido eliminada correctamente.");

            return "redirect:/registro-login";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorEliminar", e.getMessage());
            return "redirect:/perfil";
        }
    }

}
