package com.controladores;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.model.Producto;
import com.repository.ProductoRepository;
import com.service.AzureBlobService;
import com.service.CustomUserDetails;

@Controller
@RequestMapping
public class ProductoController {

	private final ProductoRepository productoRepository;
	private final AzureBlobService azureBlobService;


    public ProductoController(ProductoRepository productoRepository, AzureBlobService azureBlobService) {
        this.productoRepository = productoRepository; // ✔ Spring lo inyecta
        this.azureBlobService = azureBlobService;
    }
	
    @PostMapping("/admin/productos/crear")
    public String crearProducto(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam BigDecimal precio,
            @RequestParam("archivoProducto") MultipartFile archivoProducto,
            @RequestParam("imagenProducto") MultipartFile imagenProducto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws IOException {

        // 1️⃣ Subir archivo del producto
        String urlArchivoProducto = azureBlobService.subirArchivo(
                archivoProducto, "productos"
        );

        // 2️⃣ Subir imagen del card
        String urlImagen = azureBlobService.subirArchivo(
                imagenProducto, "imagenes"
        );

        // 3️⃣ Crear producto
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setDisponible(true);
        producto.setRutaArchivo(urlArchivoProducto);
        producto.setRutaImagen(urlImagen);
        producto.setUsuario(userDetails.getUsuario());

        productoRepository.save(producto);

        return "redirect:/infografias";
    }

    @PostMapping("/admin/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // 🔥 Eliminar archivos en Azure
        azureBlobService.eliminarBlobDesdeUrl(producto.getRutaArchivo(), "productos");
        azureBlobService.eliminarBlobDesdeUrl(producto.getRutaImagen(), "imagenes");

        // 🔥 Eliminar de BD
        productoRepository.delete(producto);

        return "redirect:/infografias";
    }

}
