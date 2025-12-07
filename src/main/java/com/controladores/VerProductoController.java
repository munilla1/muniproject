package com.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.model.Producto;
import com.model.Usuario;
import com.repository.PaymentRepository;
import com.repository.ProductoRepository;
import com.service.CustomUserDetails;
import com.service.AzureBlobService;

@Controller
public class VerProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AzureBlobService azureBlobService;

    @GetMapping("/ver/{productoId}")
    public String verProducto(
            @PathVariable Long productoId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        Usuario usuario = userDetails.getUsuario();

        boolean haPagado = paymentRepository
                .findByUsuarioIdAndStatus(usuario.getId(), "succeeded")
                .stream()
                .anyMatch(p -> p.getProducto().getId().equals(productoId));

        if (!haPagado) return "error403";

        Producto producto = productoRepository.findById(productoId).orElseThrow();

        String fileName = producto.getRutaArchivo();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        String sasUrl = azureBlobService.generarSasUrl(fileName);

        model.addAttribute("sasUrl", sasUrl);
        model.addAttribute("producto", producto);
        model.addAttribute("ext", extension);

        return "verProducto";
    }

}
