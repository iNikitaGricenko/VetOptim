package com.wolfhack.vetoptim.billing.model;

import com.wolfhack.vetoptim.common.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @ToString.Exclude
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<ChargeableTask> chargeableTasks;

    @ToString.Exclude
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<ChargeableResource> chargeableResources;

    @ToString.Exclude
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<Payment> payments;

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) {
			return false;
		}
		Invoice invoice = (Invoice) o;
		return getId() != null && Objects.equals(getId(), invoice.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}

}
