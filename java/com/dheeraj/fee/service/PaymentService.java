package com.dheeraj.fee.service;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class PaymentService {

    @Value("${razorpay.secret}")
    private String secret;

    public boolean verifySignature(String orderId,
            String paymentId,
            String razorpaySignature) throws Exception {

String payload = orderId + "|" + paymentId;

Mac sha256 = Mac.getInstance("HmacSHA256");

SecretKeySpec keySpec =
new SecretKeySpec(secret.getBytes(), "HmacSHA256");

sha256.init(keySpec);

byte[] hash = sha256.doFinal(payload.getBytes());

String generatedSignature = bytesToHex(hash);

System.out.println("Generated: " + generatedSignature);
System.out.println("Razorpay: " + razorpaySignature);

return generatedSignature.equals(razorpaySignature);
}

private String bytesToHex(byte[] bytes) {
StringBuilder sb = new StringBuilder();
for (byte b : bytes) {
sb.append(String.format("%02x", b));
}
return sb.toString();
}
}