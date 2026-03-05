package com.dheeraj.fee;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.dheeraj.fee.entity.Student;
import com.dheeraj.fee.repository.StudentRepository;

@SpringBootApplication
public class FeePaymentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeePaymentsApplication.class, args);
    }

    @Bean
    CommandLineRunner initAdmin(StudentRepository repo,
                                BCryptPasswordEncoder encoder) {
        return args -> {

            if(repo.findByEmail("admin@gmail.com").isEmpty()){

                Student admin = new Student();

                admin.setName("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                admin.setTotalFee(0);
                admin.setPaidAmount(0);
                admin.setVerified(true);

                repo.save(admin);

                System.out.println("Admin user created");
            }
        };
    }
}