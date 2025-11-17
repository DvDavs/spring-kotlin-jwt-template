package mx.evolutiondev.template.core.auth.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "refresh_tokens")
data class RefreshTokenEntity(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0,

        @Column(unique = true, nullable = false)
        var token: String = "",

        @Column(nullable = false)
        var expiryDate: Instant = Instant.now(),

        @ManyToOne
        @JoinColumn(name = "customer_id", nullable = false)
        var customer: CustomerEntity,

        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        var createdAt: Instant? = null,

        @UpdateTimestamp
        @Column(name = "updated_at")
        var updatedAt: Instant? = null
)

