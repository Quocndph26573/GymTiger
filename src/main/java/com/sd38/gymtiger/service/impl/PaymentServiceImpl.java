package com.sd38.gymtiger.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sd38.gymtiger.config.MomoPaymentConfig;
import com.sd38.gymtiger.config.VNPaymentConfig;
import com.sd38.gymtiger.config.ZaloPayConfig;
import com.sd38.gymtiger.request.MomoPaymentRequest;
import com.sd38.gymtiger.request.ZaloPaymentRequest;
import com.sd38.gymtiger.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    Gson gson = new Gson();

    @Override
    public JsonObject MomoPayCreate(Long amount, String specificAddress, String ward, String district, String city, String name, String phoneNumber, String email, String voucher) throws IOException, URISyntaxException {

        int random_id = new Random().nextInt(1000000);

        MomoPaymentRequest momoPaymentRequest = MomoPaymentRequest.builder()
                .partnerCode(MomoPaymentConfig.partnerCode).redirectUrl(MomoPaymentConfig.returnUrl)
                .ipnUrl(MomoPaymentConfig.ipnUrl).amount(amount)
                .requestType("captureWallet").requestId(String.valueOf(random_id))
                .orderId(String.valueOf(random_id)).orderInfo("GymTiger - Thanh Toán Đơn Hàng #" + random_id)
                .lang("vi").extraData("").build();

        String signature = momoPaymentRequest.signatureGen(MomoPaymentConfig.accessKey, MomoPaymentConfig.secretKey);
        momoPaymentRequest.setSignature(signature);

        String jsonPost = gson.toJson(momoPaymentRequest);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(MomoPaymentConfig.paymentUrl);
        StringEntity requestEntity = new StringEntity(jsonPost, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JsonObject jsonResult = gson.fromJson(resultJsonStr.toString(), JsonObject.class);

//        System.out.println(jsonResult.toString());

//        System.out.println(jsonResult.get("payUrl"));

//        return resultJsonStr.toString();
        String payUrl = jsonResult.get("payUrl").toString().replaceAll("\"", "");
        JsonObject returnJson = new JsonObject();
        returnJson.addProperty("payUrl", payUrl);
        returnJson.addProperty("specificAddress",specificAddress);
        returnJson.addProperty("ward",ward);
        returnJson.addProperty("district",district);
        returnJson.addProperty("city",city);
        returnJson.addProperty("name",name);
        returnJson.addProperty("phoneNumber",phoneNumber);
        returnJson.addProperty("email",email);
        returnJson.addProperty("voucher",voucher);
        System.out.println(returnJson.toString());
        return returnJson;
    }

    @Override
    public JsonObject zalopayCreate(Long amount, String specificAddress, String ward, String district, String city, String name, String phoneNumber, String email, String voucher) throws IOException {

        int random_id = new Random().nextInt(1000000);

        JsonObject embed_data = new JsonObject();
        embed_data.addProperty("redirecturl", "http://localhost:3000/zalo/return");

        ZaloPaymentRequest zaloPaymentRequest = ZaloPaymentRequest.builder()
                .app_id(Long.valueOf(ZaloPayConfig.appid))
                .app_trans_id(ZaloPayConfig.getCurrentTimeString("yyMMdd") + "_" + random_id)
                .app_time(Long.valueOf(String.valueOf(System.currentTimeMillis())))
                .app_user("SD_38")
                .amount(amount)
                .description("GymTiger - Thanh Toán Đơn Hàng #" + random_id)
                .bank_code("")
                .item("[]")
                .embed_data(embed_data.toString())
                .build();
        zaloPaymentRequest.setMac(zaloPaymentRequest.signatureGen(ZaloPayConfig.key1));

        String jsonPost = gson.toJson(zaloPaymentRequest);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ZaloPayConfig.endpointCreateOrder);

        StringEntity requestEntity = new StringEntity(jsonPost, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JsonObject jsonResult = gson.fromJson(resultJsonStr.toString(), JsonObject.class);
        String payUrl = jsonResult.get("order_url").toString().replaceAll("\"", "");

        JsonObject returnJson = new JsonObject();

        returnJson.addProperty("payUrl",payUrl);
        returnJson.addProperty("specificAddress",specificAddress);
        returnJson.addProperty("ward",ward);
        returnJson.addProperty("district",district);
        returnJson.addProperty("city",city);
        returnJson.addProperty("name",name);
        returnJson.addProperty("phoneNumber",phoneNumber);
        returnJson.addProperty("email",email);
        returnJson.addProperty("voucher",voucher);

        System.out.println(returnJson.toString());

        return returnJson;
    }

    @Override
    public JsonObject vnpayCreate(HttpServletRequest req, Long price, String specificAddress, String ward, String district, String city, String name, String phoneNumber, String email, String voucher) throws UnsupportedEncodingException {
        String vnp_Version = VNPaymentConfig.vnp_Version;
        String vnp_Command = VNPaymentConfig.vnp_Command;
        String orderType = VNPaymentConfig.orderType;
        long amount = price * 100;
        String bankCode = VNPaymentConfig.bankCode;

        String vnp_TxnRef = VNPaymentConfig.getTransactionNumber(8);
        String vnp_IpAddr = VNPaymentConfig.getIpAddress(req);

        String vnp_TmnCode = VNPaymentConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "GymTiger - Thanh Toán Đơn Hàng #" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = req.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", VNPaymentConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPaymentConfig.hmacSHA512(VNPaymentConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String payUrl = VNPaymentConfig.vnp_PayUrl + "?" + queryUrl;

        JsonObject returnJson = new JsonObject();
        returnJson.addProperty("payUrl", payUrl);
        returnJson.addProperty("specificAddress",specificAddress);
        returnJson.addProperty("ward",ward);
        returnJson.addProperty("district",district);
        returnJson.addProperty("city",city);
        returnJson.addProperty("name",name);
        returnJson.addProperty("phoneNumber",phoneNumber);
        returnJson.addProperty("email",email);
        returnJson.addProperty("voucher",voucher);
        return returnJson;
    }
}
