package com.dheeraj.fee.controller;

import com.dheeraj.fee.repository.PaymentRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final PaymentRepository repository;

    public AdminController(PaymentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/stats")
    public Map<String,Object> stats(){

        Map<String,Object> map = new HashMap<>();

        map.put("total", repository.count());
        map.put("success", repository.countByStatus("SUCCESS"));
        map.put("failed", repository.countByStatus("FAILED"));

        return map;
    }

    @GetMapping("/payments")
    public List<Map<String,Object>> getPayments(){

        List<Object[]> rows = repository.getPaymentsWithStudent();

        List<Map<String,Object>> list = new ArrayList<>();

        for(Object[] r : rows){

            Map<String,Object> map = new HashMap<>();

            map.put("id", r[0]);
            map.put("studentName", r[1]);
            map.put("studentEmail", r[2]);
            map.put("orderId", r[3]);
            map.put("paymentId", r[4]);
            map.put("amount", r[5]);
            map.put("status", r[6]);

            list.add(map);
        }

        return list;
    }


    @GetMapping("/download-report")
    public ResponseEntity<byte[]> downloadReport() throws Exception {

        List<Object[]> rows = repository.getPaymentsWithStudent();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Acharya Bangalore B School")
                .setBold().setFontSize(18));

        document.add(new Paragraph("Student Payment Report\n"));

        for(Object[] r : rows){

            document.add(new Paragraph(
                    "Student: " + r[1] +
                    " | Email: " + r[2] +
                    " | Payment ID: " + r[4] +
                    " | Amount: ₹" + r[5] +
                    " | Status: " + r[6]
            ));
        }

        document.close();

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=payment-report.pdf")
                .header("Content-Type","application/pdf")
                .body(baos.toByteArray());
    }

}