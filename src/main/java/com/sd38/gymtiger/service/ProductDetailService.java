package com.sd38.gymtiger.service;

import com.sd38.gymtiger.response.ProductDetailSearchResponse;
import com.sd38.gymtiger.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface ProductDetailService {
    BigInteger getTotalQuantityProductDetail();

    BigInteger getTotalQuantityByProductId(Integer productId);

    Page<ProductDetail> getAllPT(Integer page);

    Boolean add(Integer productId, Integer quantity, BigDecimal price, Integer sizeId, Integer colorId) throws IOException;

    ProductDetail update(Integer id, Integer productId, Integer quantity, BigDecimal price, Integer sizeId, Integer colorId);

    ProductDetail delete(Integer id);

    ProductDetail getOne(Integer id);


    Page<ProductDetailSearchResponse> getProductByPriceAndSizeIdAndColorId(int page, Integer productId, BigDecimal priceMinDiscount, BigDecimal priceMaxDiscount, Integer sizeId, Integer colorId);

    String importExcelProduct(MultipartFile file, Integer productId) throws IOException;

//    int getTotalPage(int page, Integer productId, BigDecimal priceMinDiscount, BigDecimal priceMaxDiscount, Integer sizeId, Integer colorId);

    Page<ProductDetailSearchResponse> getProductByPriceAndSizeIdAndColorIdDeleted(int page, Integer productId, BigDecimal priceMin, BigDecimal priceMax, Integer sizeId, Integer colorId);

    ProductDetail restore(Integer id);

    int getTotalPageDeleted(int page, Integer productId, BigDecimal priceMin, BigDecimal priceMax, Integer sizeId, Integer colorId);

    byte[] getProductDetail(Integer id);

    List<ProductDetail> getProductDetailNoDeleteResponse(List<ProductDetail> listProductDetail);

    Page<ProductDetail> findAllByProductNameAndStatus(String name, Integer status, Integer qty, Pageable pageable);

    List<ProductDetail> findAllActive();

    List<ProductDetail> locSpTaiQuay(String s, String colorCode, String matrCode, String sizeName);

    void simplizedUpdate(Integer id, ProductDetail productDetail);
}
