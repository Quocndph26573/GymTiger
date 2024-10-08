package com.sd38.gymtiger.request;

import com.sd38.gymtiger.config.ZaloPayConfig;
import com.sd38.gymtiger.utils.payment.HMACUtil;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZaloPaymentRequest {

    private Long app_id;
    private String app_trans_id;
    private Long app_time;
    private String app_user;
    private Long amount;
    private String description;
    private String bank_code;
    private String item;
    private String embed_data;
    private String mac;

    public String signatureGen(String secretKey) {
        //app_id +”|”+ app_trans_id +”|”+ app_user +”|”+ amount +"|"+ app_time +”|”+ embed_data +"|"+ item
        String rawHash = this.app_id + "|" +
                this.app_trans_id + "|" +
                this.app_user + "|" +
                this.amount + "|" +
                this.app_time + "|" +
                this.embed_data + "|" +
                this.item;

        return HMACUtil.HMACSHA256Encode(ZaloPayConfig.key1, rawHash);
    }

}
