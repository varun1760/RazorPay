package com.rao.RazorPay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@Slf4j
public class RazorPayApplication {

	public static void main(String[] args) {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);

            byte[] key = kg.generateKey().getEncoded();

            System.out.println(Base64.getEncoder().encodeToString(key));
        } catch (NoSuchAlgorithmException e) {
            log.warn("No such algorithm");
        }
        SpringApplication.run(RazorPayApplication.class, args);
	}

}
