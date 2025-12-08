package com.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.model.PaymentEntity;
import com.model.PaymentRequest;
import com.model.PaymentResponse;
import com.model.Producto;
import com.model.Usuario;
import com.repository.PaymentRepository;
import com.repository.ProductoRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProductoRepository productoRepository;
    
    @Value("${stripe.secret.key}")
    private String secretKey;
    
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("usd", "eur");

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public PaymentService(PaymentRepository paymentRepository, ProductoRepository productoRepository) {
        this.paymentRepository = paymentRepository;
        this.productoRepository = productoRepository;
    }

    public PaymentResponse processPayment(PaymentRequest request) throws StripeException {
        System.out.println("📥 Procesando pago para producto ID: " + request.productoId());

        Long productoId = Long.valueOf(request.productoId());
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no válido"));

        String currency = request.currency().toLowerCase();
        if (!SUPPORTED_CURRENCIES.contains(currency)) {
            throw new IllegalArgumentException("Moneda no soportada: " + currency);
        }

        BigDecimal precioFijado = producto.getPrecio();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
        	.setAmount(request.amount())
        	.setCurrency(currency)
            .setPaymentMethod(request.paymentMethodId())
            .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC)
            .setConfirm(true)
            .setReturnUrl("http://localhost:8080/result")
            .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        if (!paymentIntent.getStatus().equals("succeeded")) {
            throw new RuntimeException("El pago no fue confirmado correctamente.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Usuario usuario = userDetails.getUsuario();

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setProducto(producto);
        paymentEntity.setAmount(BigDecimal.valueOf(request.amount()).divide(BigDecimal.valueOf(100)));
        paymentEntity.setCurrency(request.currency());
        paymentEntity.setDescription(request.description());
        paymentEntity.setStatus(paymentIntent.getStatus());
        paymentEntity.setStripeEmail(request.stripeEmail());
        paymentEntity.setStripeToken(request.paymentMethodId());
        paymentEntity.setPaymentIntentId(paymentIntent.getId());
        paymentEntity.setCreatedAt(LocalDateTime.now());
        paymentEntity.setUsuario(usuario);
        
        System.out.println("🔑 Stripe API Key en runtime: " + Stripe.apiKey);

        paymentRepository.save(paymentEntity);

        return new PaymentResponse(
            paymentIntent.getId(),
            Optional.ofNullable(paymentIntent.getAmount()).orElse(0L),
            request.currency(),
            paymentIntent.getStatus()
        );
    }
}