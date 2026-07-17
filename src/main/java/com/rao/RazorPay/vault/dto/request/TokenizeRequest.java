package com.rao.RazorPay.vault.dto.request;

import com.rao.RazorPay.vault.validation.ExpiryYear;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;

import java.util.UUID;

public record TokenizeRequest(
        @NotBlank(message = "Pan is required")
        @LuhnCheck(message = "Invalid card number")
        @Pattern(regexp = "^[0-9]{13,19}$", message = "Invalid pan length")
        String pan,

        @NotBlank(message = "cvv is required")
        @Pattern(regexp = "^[0-9]{3,4}$", message = "Invalid cvv length")
        String cvv,

        @NotNull(message = "Expiry month is required")
        @Min(value = 1, message = "Expiry month must be between 1 to 12")
        @Max(value = 12, message = "Expiry month must be between 1 to 12")
        Integer expiryMonth,

        @NotNull(message = "Expiry year is required")
        @ExpiryYear     // custom validation
        Integer expiryYear,

        UUID customerId,

        @Size(min = 3, message = "Card holder name should be at least 3 characters")
        String cardHolderName
) {
}
