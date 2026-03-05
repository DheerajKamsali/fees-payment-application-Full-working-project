
package com.dheeraj.fee.controller;

import com.dheeraj.fee.config.RazorpayConfig;
import com.dheeraj.fee.entity.Payment;
import com.dheeraj.fee.entity.Student;
import com.dheeraj.fee.repository.PaymentRepository;
import com.dheeraj.fee.repository.StudentRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.awt.PageAttributes.MediaType;
import java.io.ByteArrayOutputStream;
import org.springframework.http.HttpHeaders ;  // ✅ CORRECT
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final RazorpayConfig razorpayConfig;

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.secret}")
    private String secret;

    public PaymentController(PaymentRepository paymentRepository,
                             StudentRepository studentRepository,
                             RazorpayClient razorpayClient, RazorpayConfig razorpayConfig) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
        this.razorpayClient = razorpayClient;
        this.razorpayConfig = razorpayConfig;
    }

    
    @GetMapping("/download-receipt")
    public ResponseEntity<byte[]> downloadReceipt(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails)
            throws Exception {

        Student student = studentRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow();

        List<Payment> payments =
                paymentRepository.findByStudentId(student.getId());

        Payment latest = payments.get(payments.size()-1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("ACHARYA BANGALORE B SCHOOL")
                .setBold()
                .setFontSize(18));

        document.add(new Paragraph("Official Fee Receipt\n"));

        document.add(new Paragraph("Student Name: " + student.getName()));
        document.add(new Paragraph("Email: " + student.getEmail()));

        document.add(new Paragraph("Payment ID: " + latest.getPaymentId()));
        document.add(new Paragraph("Amount Paid: ₹ " + latest.getAmount()));

        document.add(new Paragraph("Date: " + LocalDate.now()));

        document.close();

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=receipt.pdf")
                .header("Content-Type","application/pdf")
                .body(baos.toByteArray());
    }

       
    // ✅ CREATE ORDER
    @PostMapping("/create-order")
    public Map<String, Object> createOrder(@RequestParam int amount) throws Exception {

        JSONObject options = new JSONObject();
        options.put("amount", amount * 100); // amount in paise
        options.put("currency", "INR");
        options.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(options);

        Map<String, Object> response = new HashMap<>();
        response.put("id", order.get("id"));
        response.put("amount", order.get("amount"));

        return response;
    }

    // ✅ VERIFY + SAVE PAYMENT
    @PostMapping("/verify")
    public String verifyPayment(@RequestBody Payment payment,
                                @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        boolean isValid = verifySignature(
                payment.getOrderId(),
                payment.getPaymentId(),
                payment.getSignature()
        );

        if (!isValid) {
            return "Payment Tampered!";
        }

        // Get logged in student
        Student student = studentRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow();

        payment.setStudentId(student.getId());
        payment.setStatus("SUCCESS");

        paymentRepository.save(payment);

        // Update student paid amount
        student.setPaidAmount(student.getPaidAmount() + payment.getAmount());
        studentRepository.save(student);

        return "Payment Successful & Saved";
    }

    // ✅ SIGNATURE VERIFICATION
    private boolean verifySignature(String orderId,
                                    String paymentId,
                                    String razorpaySignature) throws Exception {

        String payload = orderId + "|" + paymentId;

        Mac sha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec =
                new SecretKeySpec(secret.getBytes(), "HmacSHA256");

        sha256.init(keySpec);

        byte[] hash = sha256.doFinal(payload.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }

        String generatedSignature = sb.toString();

        return generatedSignature.equals(razorpaySignature);
    }
}
