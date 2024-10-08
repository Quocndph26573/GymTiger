package com.sd38.gymtiger.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDetailSearchResponse {
    private Integer id;
    private String code;
    private BigDecimal price;
    private BigDecimal priceDiscount;
    private Integer quantity;
    private String sizeName;
    private String colorName;
}
