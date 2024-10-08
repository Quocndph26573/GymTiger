package com.sd38.gymtiger.repository;

import com.sd38.gymtiger.dto.admin.ProductDto;
import com.sd38.gymtiger.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllByAndStatusOrderByIdDesc(Integer status);

    Product findTopByOrderByIdDesc();

    @Query(value = "SELECT p.id as id, " +
            "p.code as code, " +
            "p.name as name, " +
            "p.description as description, " +
            "p.status as status, " +
            "p.brand.name as brandName, " +
            "p.category.name as categoryName, " +
            "p.form.name as formName, " +
            "p.material.name as materialName " +
            " FROM Product p " +
            " WHERE p.status IN (1,2) ORDER BY p.updateDate DESC")
    Page<ProductDto> getAllProduct(Pageable pageable);

    @Query(value = "SELECT p.id as id, " +
            "p.code as code, " +
            "p.name as name, " +
            "p.description as description, " +
            "p.status as status, " +
            "p.brand.name as brandName, " +
            "p.category.name as categoryName, " +
            "p.form.name as formName, " +
            "p.material.name as materialName " +
            " FROM Product p WHERE (p.name LIKE %:productName% OR p.name IS NULL) " +
//            " AND (p.description LIKE %:description% OR p.description IS NULL) " +
            " AND (p.brand.id=:brandId OR :brandId IS NULL) " +
            " AND (p.material.id=:materialId OR :materialId IS NULL) " +
            " AND (p.category.id=:categoryId OR :categoryId IS NULL) " +
            " AND (p.form.id=:formId OR :formId IS NULL) " +
            " AND p.status IN (1, 2) ORDER BY p.updateDate DESC")
    Page<ProductDto> search(Pageable pageable, Integer brandId, Integer categoryId, Integer formId, Integer materialId, String productName);


    @Query(value = "SELECT p.id as id, " +
            "p.code as code, " +
            "p.name as name, " +
            "p.description as description, " +
            "p.status as status, " +
            "p.brand.name as brandName, " +
            "p.category.name as categoryName, " +
            "p.form.name as formName, " +
            "p.material.name as materialName " +
            " FROM Product p WHERE (p.name LIKE %:productName% OR p.name IS NULL) " +
//            " AND (p.description LIKE %:description% OR p.description IS NULL) " +
            " AND (p.brand.id=:brandId OR :brandId IS NULL) " +
            " AND (p.material.id=:materialId OR :materialId IS NULL) " +
            " AND (p.category.id=:categoryId OR :categoryId IS NULL) " +
            " AND (p.form.id=:formId OR :formId IS NULL) " +
            " AND p.status = 1 ORDER BY p.updateDate DESC")
    Page<ProductDto> searchProductDefault(Pageable pageable, Integer brandId, Integer categoryId, Integer formId, Integer materialId, String productName);


    @Query(value = "SELECT p.id as id, " +
            "p.code as code, " +
            "p.name as name, " +
            "p.description as description, " +
            "p.status as status, " +
            "p.brand.name as brandName, " +
            "p.category.name as categoryName, " +
            "p.form.name as formName, " +
            "p.material.name as materialName " +
            " FROM Product p WHERE (p.name LIKE %:productName% OR p.name IS NULL) " +
//            " AND (p.description LIKE %:description% OR p.description IS NULL) " +
            " AND (p.brand.id=:brandId OR :brandId IS NULL) " +
            " AND (p.material.id=:materialId OR :materialId IS NULL) " +
            " AND (p.category.id=:categoryId OR :categoryId IS NULL) " +
            " AND (p.form.id=:formId OR :formId IS NULL) " +
            " AND p.status = 2 ORDER BY p.updateDate DESC")
    Page<ProductDto> searchProductDiscount(Pageable pageable, Integer brandId, Integer categoryId, Integer formId, Integer materialId, String productName);


    @Query(value = "SELECT p.id as id, " +
            "p.code as code, " +
            "p.name as name, " +
            "p.status as status, " +
            "p.description as description, " +
            "p.brand.name as brandName, " +
            "p.category.name as categoryName, " +
            "p.form.name as formName, " +
            "p.material.name as materialName " +
            " FROM Product p " +
            " WHERE p.status = 0 ORDER BY p.updateDate DESC")
    Page<ProductDto> getAllProductDeleted(Pageable pageable);

    @Query(value = "SELECT p.id as id, " +
            "p.code as code, " +
            "p.name as name, " +
            "p.description as description, " +
            "p.status as status, " +
            "p.brand.name as brandName, " +
            "p.category.name as categoryName, " +
            "p.form.name as formName, " +
            "p.material.name as materialName " +
            " FROM Product p WHERE (p.name LIKE %:productName% OR p.name IS NULL) " +
//            " AND (p.description LIKE %:description% OR p.description IS NULL) " +
//            " AND (p.price >= :priceMin AND p.price <= :priceMax)" +
            " AND (p.brand.id=:brandId OR :brandId IS NULL) " +
            " AND (p.material.id=:materialId OR :materialId IS NULL) " +
            " AND (p.category.id=:categoryId OR :categoryId IS NULL) " +
            " AND (p.form.id=:formId OR :formId IS NULL) " +
            " AND p.status = 0 ORDER BY p.updateDate DESC")
    Page<ProductDto> searchProductDeleted(Pageable pageable, Integer brandId, Integer categoryId, Integer formId, Integer materialId, String productName);

}
