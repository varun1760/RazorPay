package com.rao.RazorPay.payment.simulator;

import com.rao.RazorPay.common.enums.ChaosMode;
import com.rao.RazorPay.common.enums.PaymentStatus;
import com.rao.RazorPay.common.util.RandomizerUtil;
import com.rao.RazorPay.payment.config.SimulatorConfig;
import com.rao.RazorPay.payment.entity.Payment;
import com.rao.RazorPay.payment.repository.PaymentRepository;
import com.rao.RazorPay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankCallbackSimulator {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

//    @Scheduled(fixedRateString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallback() {
        LocalDateTime globalWindow =  LocalDateTime.now().minusSeconds(1);
        List<Payment> candidates = paymentRepository.findByStatusAndCreatedAtBefore(
                PaymentStatus.AUTHORIZING, globalWindow);

        if (candidates.isEmpty()) {
            return;
        }
        for (Payment payment : candidates) {
            simulateCallback(payment);
        }
    }

    private void simulateCallback(Payment payment) {
        SimulatorConfig.MethodSimulatorConfig methodConfig = simulatorConfig.configFor(payment.getMethod());

        LocalDateTime dueAt = dueAt(payment,  methodConfig);

        if (LocalDateTime.now().isBefore(dueAt)) {
            return;
        }
        ChaosMode chaosMode = simulatorConfig.getChaosMode();

        switch (chaosMode) {
            case SUCCESS -> resolve(payment, true);
            case FAILURE -> resolve(payment, false);
            case TIMEOUT -> log.debug("BankCallBack Simulator: Payment Timed out");
            case NORMAL, SLOW ->  resolve(payment, shouldApproved(payment, methodConfig));
        }
    }

    private LocalDateTime dueAt(Payment payment, SimulatorConfig.MethodSimulatorConfig methodConfig) {
        int range = methodConfig.getMaxDelaySeconds() - methodConfig.getMinDelaySeconds();
        int delaySeconds = methodConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range + 1);

        if (simulatorConfig.getChaosMode() == ChaosMode.SLOW) {
            delaySeconds *= 2;
        }
        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }

    private void resolve(Payment payment, Boolean approved) {
        if (approved) {
            String bankReference = "SIMULATOR_BANK_REFERENCE_" + RandomizerUtil.randomBase64(8);
            paymentService.resolveAuthorization(payment.getId(), true, bankReference, null, null);
        } else {
            paymentService.resolveAuthorization(payment.getId(), false, null, "SIM_BANK_ERROR_CODE", "Simulated Bank Declined");
        }
    }

    private boolean shouldApproved(Payment payment, SimulatorConfig.MethodSimulatorConfig methodConfig) {
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return bucket < methodConfig.getSuccessRate();
    }
}
