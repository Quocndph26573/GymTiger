
package com.sd38.gymtiger.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OfflineCart {

    private String detailProductId;

    private Integer qty;

}
