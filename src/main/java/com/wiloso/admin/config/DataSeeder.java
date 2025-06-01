package com.wiloso.admin.config;

import com.wiloso.admin.model.User;
import com.wiloso.admin.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.IntStream;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                Random random = new Random();

                // Direktur
                User direktur = new User();
                direktur.setName("Direktur Utama");
                direktur.setEmail("direktur@example.com");
                direktur.setPassword(encoder.encode("password123"));
                direktur.setRole("Direktur");
                direktur.setCreatedTime(LocalDateTime.now());
                userRepository.save(direktur);

                // 20 Staf
                IntStream.rangeClosed(1, 20).forEach(i -> {
                    User staf = new User();
                    staf.setName("Staf " + i);
                    staf.setEmail("staf" + i + "@example.com");
                    staf.setPassword(encoder.encode("password" + i));
                    staf.setRole("Staff");
                    staf.setCreatedTime(LocalDateTime.now().minusDays(random.nextInt(30)));
                    userRepository.save(staf);
                });

                System.out.println("Seeder: 1 Direktur dan 20 Staf berhasil ditambahkan.");
            }
        };
    }
}
