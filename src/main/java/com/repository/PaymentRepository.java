package com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.model.PaymentEntity;
import com.model.Usuario;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
	
	Optional<PaymentEntity> findByPaymentIntentId(String paymentIntentId);

	List<PaymentEntity> findByUsuarioIdAndStatus(Long usuarioId, String status);
	
	List<PaymentEntity> findByUsuario(Usuario usuario);

}