package com.newpackage.neetcounsel.service;

import com.razorpay.Order;
import com.razorpay.Utils;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpayKeySecret;

    public Order createOrder(Double amount) throws Exception {
        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", true);

        return client.orders.create(orderRequest);
    }
    
    public boolean verifySignature(String orderId, String paymentId, String razorpaySignature) {
        try {
        	JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", razorpaySignature);
            boolean verified=Utils.verifyPaymentSignature(options, razorpayKeySecret);
            return verified;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
