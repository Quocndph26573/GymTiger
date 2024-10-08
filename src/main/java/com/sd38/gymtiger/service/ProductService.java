package com.sd38.gymtiger.service;

import com.sd38.gymtiger.dto.admin.ProductDto;
import com.sd38.gymtiger.model.Product;
import com.sd38.gymtiger.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<Product> getAll();
    Page<Product> getAll(Integer page);
    Page<ProductDto> getAll(int page);

    Boolean add(String productName, String description, Integer materialId, Integer categoryId, Integer brandId, Integer formId);

    Product update(Integer id, String code, String productName, String description, Integer materialId, Integer categoryId, Integer brandId, Integer formId);

    Product delete(Integer id);

    Product getOne(Integer id);

    Page<ProductDto> search(Integer materialId, Integer brandId, Integer formId, Integer categoryId, String productName, int page);

    Page<ProductDto> searchProductDefault(Integer materialId, Integer brandId, Integer formId, Integer categoryId, String productName, int page);

    Page<ProductDto> searchProductDiscount(Integer materialId, Integer brandId, Integer formId, Integer categoryId, String productName, int page);

    Integer importExcelProduct(MultipartFile file) throws IOException;

    Page<ProductDto> getAllProductDeleted(int page);

    Page<ProductDto> searchProductDeleted(Integer materialId, Integer brandId, Integer formId, Integer categoryId, String productName, int page);

    Product restore(Integer id);

    void addFinal(Product product, List<ProductDetail> listProductDetail, List<MultipartFile> files) throws IOException;

    void updateFinal(Product productBefore, Product productUpdate, List<ProductDetail> listProductDetailUpdate,
                     List<MultipartFile> filesUpdate, List<Integer> imageRemoveIds, List<Integer> productDetailRemoveIds) throws IOException;

    void update(Product product, Integer productId);
}
