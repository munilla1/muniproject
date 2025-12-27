package com.controladores;

import java.util.List;
import java.util.Objects;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.DTOs.EliminarDTO;
import com.DTOs.RegistroDTO;
import com.model.PaymentEntity;
import com.model.Producto;
import com.model.Usuario;
import com.repository.PaymentRepository;
import com.service.CustomUserDetails;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final PaymentRepository paymentRepository;

    public PerfilController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping
    public String mostrarPerfil(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Usuario usuario = userDetails.getUsuario();

        List<PaymentEntity> pagos = paymentRepository
                .findByUsuarioIdAndStatus(usuario.getId(), "succeeded");

        if (pagos == null) {
            pagos = List.of();
        }

        List<Producto> productosComprados = pagos.stream()
                .map(PaymentEntity::getProducto)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        model.addAttribute("usuarioNombre", usuario.getUsername());
        model.addAttribute("registroDTO", new RegistroDTO());
        model.addAttribute("eliminarDTO", new EliminarDTO());
        model.addAttribute("productosComprados", productosComprados);

        return "perfil";
    }
}

