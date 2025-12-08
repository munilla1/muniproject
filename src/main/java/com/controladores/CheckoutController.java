package com.controladores;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.model.PaymentRequest;
import com.model.PaymentResponse;
import com.model.Producto;
import com.repository.ProductoRepository;
import com.service.PaymentService;
import com.stripe.exception.StripeException;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    private final PaymentService paymentService;
    private final ProductoRepository productoRepository;

    public CheckoutController(PaymentService paymentService, ProductoRepository productoRepository) {
        this.paymentService = paymentService;
        this.productoRepository = productoRepository;
    }

    @GetMapping("/checkout")
    public String mostrarCheckout(@RequestParam("productoId") Long productoId, Model model) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        model.addAttribute("productoId", producto.getId());
        model.addAttribute("precio", producto.getPrecio());
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currencies", List.of(Currency.getInstance("EUR")));

        return "checkout";
    }
    
    @PostMapping(value = "/checkout", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> procesarPago(@RequestBody Map<String, Object> requestBody, HttpSession session) {
        System.out.println("📥 Datos recibidos en el backend: " + requestBody);

        Long productoId = Long.parseLong(requestBody.get("productoId").toString());

        // 🔹 Obtener el producto de la base de datos
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

    	Long amount = producto.getPrecio().multiply(BigDecimal.valueOf(100)).longValue();

        String description = producto.getDescripcion();
        String currency = requestBody.getOrDefault("currency", "EUR").toString();
        String stripeEmail = requestBody.getOrDefault("email", "").toString();
        String paymentMethodId = requestBody.getOrDefault("paymentMethodId", "").toString();

        System.out.println("📥 Producto ID recibido: " + productoId);
        System.out.println("🔹 Email recibido: " + stripeEmail);
        System.out.println("🔹 PaymentMethodId recibido: " + paymentMethodId);

        if (paymentMethodId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("success", false, "message", "Error: Falta el método de pago."));
        }

        try {
            // 🔹 Procesar el pago con el producto dinámico
            PaymentRequest request = new PaymentRequest(producto.getId(), description, currency, stripeEmail, paymentMethodId, amount);
            PaymentResponse response = paymentService.processPayment(request);

            session.setAttribute("paymentIntentId", response.paymentIntentId());
            session.setAttribute("productoId", request.productoId());

            return ResponseEntity.ok(Map.of("success", true, "paymentId", response.paymentIntentId()));
        } catch (StripeException e) {
            System.err.println("❌ Error en Stripe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error en el pago: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Ocurrió un error inesperado."));
        }
    }

    
    @GetMapping("/result")
    public String mostrarResultado(@RequestParam(required = false) String paymentId, HttpSession session, Model model) {
        // 🔹 Recupera productoId con conversión segura
        Long productoId = (session.getAttribute("productoId") instanceof Long) 
            ? (Long) session.getAttribute("productoId") 
            : null;

        model.addAttribute("paymentIntentId", paymentId);
        model.addAttribute("productoId", productoId); // ✅ necesario para que se muestre el botón de descarga
        model.addAttribute("usuario", session.getAttribute("usuario"));

        return "result";
    }

}

