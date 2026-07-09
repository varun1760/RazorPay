package com.rao.RazorPay.merchant.entity;

import com.rao.RazorPay.common.entity.BaseEntity;
import com.rao.RazorPay.common.enums.Environment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (name = "api_key", indexes = {
        @Index (name = "idx_api_key_merchant_env", columnList = "merchant_id, environment, enabled")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiKey extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(length = 50, nullable = false, unique = true)
    private String keyId;

    @Column(length = 200, nullable = false)
    private String keySecretHash;

    @Column(length = 200)
    private String previousKeySecretHash;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Environment environment;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    private LocalDateTime lastUsedAt;
    private LocalDateTime rotatedAt;
    private LocalDateTime gracePeriodExpiresAt;
}
