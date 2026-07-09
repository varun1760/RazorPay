package com.rao.RazorPay.merchant.entity;

import com.rao.RazorPay.common.entity.BaseEntity;
import com.rao.RazorPay.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "app_user", indexes = {
        @Index(name = "idx_app_user_merchant_id", columnList = "merchant_id")
})
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class AppUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
}
