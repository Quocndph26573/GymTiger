package com.sd38.gymtiger.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class SessionCart {
    private BigDecimal totalPrice;

    private Integer totalItems;

    private Set<SessionCartItem> cartDetails;

    public void clear(){
        this.cartDetails.clear();
        this.totalPrice = BigDecimal.ZERO;
        this.totalItems = 0;
    }
}
