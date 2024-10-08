package com.sd38.gymtiger.repository;

import com.sd38.gymtiger.dto.admin.ProductPromotionDTO;
import com.sd38.gymtiger.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface productPromotion extends JpaRepository<Product, Integer> {
    @Query(value = "SELECT" +
            " P.id AS id," +
            " P.code AS code," +
            " P.name AS name," +
            " MIN(PD.price) AS minProductPrice," +
            " MAX(PD.price) AS maxProductPrice" +
            " FROM Product P" +
            " JOIN ProductDetail PD ON P.id = PD.product.id " +
            " INNER JOIN Image i ON i.product.id = P.id" +
            " WHERE P.status = 1 AND PD.status = 1 AND i.status = 1" +
            " GROUP BY P.id, P.code, P.name,P.updateDate"+
            " ORDER BY P.updateDate DESC"
    )
    List<ProductPromotionDTO> getAllProductPromotionDTO();

}
