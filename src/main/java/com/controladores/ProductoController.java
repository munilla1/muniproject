package com.controladores;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.model.Producto;
import com.repository.ProductoRepository;
import com.service.AzureBlobService;

@Controller
@RequestMapping
public class ProductoController {

	private final ProductoRepository productoRepository;
	private final AzureBlobService azureBlobService;


    public ProductoController(ProductoRepository productoRepository, AzureBlobService azureBlobService) {
        this.productoRepository = productoRepository; // ✔ Spring lo inyecta
        this.azureBlobService = azureBlobService;
    }
	
	@PostMapping("/productos/{id}/upload")
	public ResponseEntity<?> uploadArchivo(
	        @PathVariable Long id,
	        @RequestParam("file") MultipartFile file) throws IOException {
	
	    Producto producto = productoRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
	
	    // Nombre que tendrá en el Blob
	    String archivoNombre = file.getOriginalFilename();
	
	    // Subir al Blob
	    azureBlobService.uploadFile(
	            archivoNombre,
	            file.getInputStream(),
	            file.getSize(),
	            file.getContentType()
	    );
	
	    // Guardamos la ruta en la BD
	    producto.setRutaArchivo(archivoNombre);
	    productoRepository.save(producto);
	
	    return ResponseEntity.ok("Archivo subido correctamente");
	}
}
