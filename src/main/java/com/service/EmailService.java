package com.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    public void enviarCorreo(String destino, String asunto, String htmlContenido) {

        try {
            System.out.println(">>> Enviando correo a: " + destino);

            // Construcción del JSON que Brevo espera
            Map<String, Object> body = new HashMap<>();

            body.put("sender", Map.of(
                    "name", "Munirehab",
                    "email", "no-reply@munirehab.eu"   // puedes usar cualquier email verificado
            ));

            body.put("to", List.of(
                    Map.of("email", destino)
            ));

            body.put("subject", asunto);
            body.put("htmlContent", htmlContenido);

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Request
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_URL, request, String.class);

            System.out.println(">>> CORREO ENVIADO CORRECTAMENTE (Brevo API)");
            System.out.println("Respuesta Brevo: " + response.getBody());

        } catch (Exception e) {
            System.out.println(">>> ERROR AL ENVIAR CORREO (Brevo API)");
            e.printStackTrace();
        }
    }
}
