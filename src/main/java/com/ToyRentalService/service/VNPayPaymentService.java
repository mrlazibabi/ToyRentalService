package com.ToyRentalService.service;
import com.ToyRentalService.config.VNPAYConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayPaymentService {

    public String createPaymentUrl(int amount, String orderInfo, String returnUrl) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPAYConfig.getRandomNumber(8);
//        String vnp_IpAddr = VNPAYConfig.getIpAddress(request);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPAYConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount*100 ));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_returnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                try {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString())).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace(); // Log lỗi nếu xảy ra lỗi mã hóa
                }
                query.append('&');
                hashData.append('&');
            }
        }

        if (hashData.length() > 0 && hashData.charAt(hashData.length() - 1) == '&') {
            hashData.setLength(hashData.length() - 1);
        }
        if (query.length() > 0 && query.charAt(query.length() - 1) == '&') {
            query.setLength(query.length() - 1);
        }

        String vnp_SecureHash = VNPAYConfig.hmacSHA512(VNPAYConfig.vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);
        return VNPAYConfig.vnp_PayUrl + "?" + query.toString();
    }

    public int validatePaymentReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();

    // Log các tham số trả về từ VNPay
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            System.out.println("Parameter is: " + fieldName + " = " + fieldValue);  // Log giá trị của từng tham số
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signValue = VNPAYConfig.hashAllFields(fields);

        // Kiểm tra giá trị `vnp_TransactionStatus` và `vnp_ResponseCode`
        String transactionStatus = request.getParameter("vnp_TransactionStatus");
        String responseCode = request.getParameter("vnp_ResponseCode");
        System.out.println("Transaction Status: " + transactionStatus);
        System.out.println("Response Code: " + responseCode);

        // Kiểm tra điều kiện thành công
        return signValue.equals(vnp_SecureHash) && "00".equals(transactionStatus) ? 1 : 0;
}









//    public int validatePaymentReturn(HttpServletRequest request) {
//        Map<String, String> fields = new HashMap<>();
//        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
//            String fieldName = params.nextElement();
//            String fieldValue = request.getParameter(fieldName);
//            if (fieldValue != null && !fieldValue.isEmpty()) {
//                fields.put(fieldName, fieldValue);
//            }
//        }
//
//        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
//        fields.remove("vnp_SecureHash");
//        fields.remove("vnp_SecureHashType");
//
//        String signValue = VNPAYConfig.hashAllFields(fields);
//        return signValue.equals(vnp_SecureHash) && "00".equals(request.getParameter("vnp_TransactionStatus")) ? 1 : 0;
//    }public int validatePaymentReturn(HttpServletRequest request) {
//    Map<String, String> fields = new HashMap<>();
//
//    // Log các tham số trả về từ VNPay
//    for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
//        String fieldName = params.nextElement();
//        String fieldValue = request.getParameter(fieldName);
//        System.out.println("Parameter: " + fieldName + " = " + fieldValue);  // Log giá trị của từng tham số
//        if (fieldValue != null && !fieldValue.isEmpty()) {
//            fields.put(fieldName, fieldValue);
//        }
//    }
//
//    String vnp_SecureHash = request.getParameter("vnp_SecureHash");
//    fields.remove("vnp_SecureHash");
//    fields.remove("vnp_SecureHashType");
//
//    String signValue = VNPAYConfig.hashAllFields(fields);
//
//    // Kiểm tra giá trị `vnp_TransactionStatus` và `vnp_ResponseCode`
//    String transactionStatus = request.getParameter("vnp_TransactionStatus");
//    String responseCode = request.getParameter("vnp_ResponseCode");
//    System.out.println("Transaction Status: " + transactionStatus);
//    System.out.println("Response Code: " + responseCode);
//
//    // Kiểm tra điều kiện thành công
//    return signValue.equals(vnp_SecureHash) && "00".equals(transactionStatus) ? 1 : 0;
//}

}
