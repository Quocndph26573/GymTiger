package com.sd38.gymtiger.dto.common;

import java.math.BigDecimal;

public interface ProductDiscountHomeDto {

    Integer getProductId();

    String getProductName();

    BigDecimal getPrice();

    BigDecimal getPriceDiscount();

    Float getValue();


}