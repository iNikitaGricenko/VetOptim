package com.wolfhack.vetoptim.billing.repository;

import com.wolfhack.vetoptim.billing.model.Invoice;
import com.wolfhack.vetoptim.common.InvoiceStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByOwnerIdAndStatusAndIssueDateBetween(Long ownerId, InvoiceStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Optional<Invoice> findByOwnerId(Long taskId);

}
