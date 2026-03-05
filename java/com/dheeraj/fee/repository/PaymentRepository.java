package com.dheeraj.fee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dheeraj.fee.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStudentId(Long studentId);

    long countByStatus(String status);

    @Query(value = """
        SELECT 
        p.id,
        s.name,
        s.email,
        p.order_id,
        p.payment_id,
        p.amount,
        p.status
        FROM payment p
        JOIN student s ON p.student_id = s.id
        """, nativeQuery = true)
    List<Object[]> getPaymentsWithStudent();

}