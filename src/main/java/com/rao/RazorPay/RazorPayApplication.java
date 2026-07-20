package com.rao.RazorPay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableScheduling
@Slf4j
public class RazorPayApplication {

	public static void main(String[] args) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);

//            byte[] masterKey = keyGenerator.generateKey().getEncoded();
//            System.out.println("master-key" +  Base64.getEncoder().encodeToString(masterKey));
//
//            byte[] jwtKey = keyGenerator.generateKey().getEncoded();
//            System.out.println("secret-key" +  Base64.getEncoder().encodeToString(jwtKey));

            // Master key
            SecretKey masterKey = keyGenerator.generateKey();
            System.out.println("master-key=" +
                    Base64.getEncoder().encodeToString(masterKey.getEncoded()));

            // JWT secret key
            SecretKey jwtKey = keyGenerator.generateKey();
            String jwtSecret = "don't_use_in_prod_" +
                    Base64.getUrlEncoder().withoutPadding()
                            .encodeToString(jwtKey.getEncoded());

            System.out.println("secret-key=" + jwtSecret);
        } catch (NoSuchAlgorithmException e) {
            log.warn("No such algorithm");
        }
        SpringApplication.run(RazorPayApplication.class, args);
	}

}
