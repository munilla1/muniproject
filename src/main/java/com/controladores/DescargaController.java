package com.controladores;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.model.Producto;
import com.model.Usuario;
import com.repository.PaymentRepository;
import com.repository.ProductoRepository;
import com.service.CustomUserDetails;
import com.service.AzureBlobService;

@RestController
public class DescargaController {

    private final AzureBlobService azureBlobService;
    private final ProductoRepository productoRepository;
    private final PaymentRepository paymentRepository;

    public DescargaController(
            AzureBlobService azureBlobService,
            ProductoRepository productoRepository,
            PaymentRepository paymentRepository) {

        this.azureBlobService = azureBlobService;
        this.productoRepository = productoRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/descargar/{productoId}")
    public ResponseEntity<byte[]> descargar(
            @PathVariable Long productoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {

        Usuario usuario = userDetails.getUsuario();

        boolean haPagado = paymentRepository
                .findByUsuarioIdAndStatus(usuario.getId(), "succeeded")
                .stream()
                .anyMatch(p -> p.getProducto().getId().equals(productoId));

        if (!haPagado) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Producto producto = productoRepository.findById(productoId).orElseThrow();

        // 🔥 EXTRAER NOMBRE DEL BLOB DESDE LA URL
        String blobUrl = producto.getRutaArchivo();
        String fileName = blobUrl.substring(blobUrl.lastIndexOf("/") + 1);

        // 🔥 DESCARGA CORRECTA
        byte[] datos = azureBlobService.downloadFile("productos", fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.add(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + producto.getNombre() + "\""
        );
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(datos);
    }

}
