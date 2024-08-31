package com.sd38.gymtiger.config;

import java.io.Serializable;

public class MomoPaymentConfig implements Serializable {

    public static String partnerCode = "MOMOBKUN20180529";
    public static String returnUrl = "http://localhost:3000/momo/return";
    public static String ipnUrl = "http://localhost:3000/api/payment/momo/ipn";
    public static String accessKey = "klm05TvNBzhg7h7j";
    public static String secretKey = "at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa";
    public static String paymentUrl = "https://test-payment.momo.vn/v2/gateway/api/create";

}
