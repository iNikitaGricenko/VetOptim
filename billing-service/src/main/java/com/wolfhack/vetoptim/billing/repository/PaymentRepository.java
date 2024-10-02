package com.wolfhack.vetoptim.billing.repository;

import com.wolfhack.vetoptim.billing.model.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.invoice.ownerId = :ownerId")
    List<Payment> findByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

	List<Payment> findPaymentsByInvoiceOwnerId(Long ownerId, Pageable pageable);

}
