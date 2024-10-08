package com.sd38.gymtiger.repository;

import com.sd38.gymtiger.dto.admin.ProductDetailDto;
import com.sd38.gymtiger.model.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {
    Page<ProductDetail> findAllByAndStatusOrderByIdDesc(Pageable pageable, Integer status);

    @Query(value = "SELECT SUM(pd.quantity) FROM ProductDetail pd WHERE pd.status =:status", nativeQuery = true)
    BigInteger getTotalQuantity(Integer status);

    @Query(value = "SELECT SUM(quantity) as totalQuantity FROM ProductDetail pd\n" +
            "INNER JOIN Product p ON pd.productId = p.id\n" +
            "WHERE p.id =:productId AND pd.status = :status", nativeQuery = true)
    BigInteger getTotalQuantityById(Integer status, Integer productId);

    List<ProductDetail> findAllByProductIdAndStatusOrderByUpdateDateDesc(Integer productId, Integer status);

    List<ProductDetail> findAllByProduct_IdOrderByIdAsc(Integer productId);

    ProductDetail findTopByOrderByIdDesc();

    ProductDetail findByIdAndStatus(Integer id, Integer status);

    @Query("SELECT c FROM ProductDetail c WHERE c.product.name LIKE %:name% AND c.status = :status AND c.quantity > :qty")
    Page<ProductDetail> searchAllByProductNameAndStatus(String name, Integer status, Integer qty, Pageable pageable);

    @Query(value = "SELECT pd FROM ProductDetail pd\n" +
            "INNER JOIN Product p ON pd.product.id = p.id\n" +
            "INNER JOIN Image i ON i.product.id = p.id\n" +
            "WHERE p.status IN (1,2) AND pd.status = 1 AND p.id =:productId\n" +
            "ORDER BY pd.price DESC")
    List<ProductDetail> getAllProductDetailByProductIdOrderByPriceDesc(Integer productId);

    @Query(value = "SELECT pd FROM ProductDetail pd\n" +
            "INNER JOIN Product p ON pd.product.id = p.id\n" +
            "INNER JOIN Image i ON i.product.id = p.id\n" +
            "WHERE p.status IN (1,2) AND pd.status = 1 AND p.id =:productId\n" +
            "ORDER BY pd.price ASC")
    List<ProductDetail> getAllProductDetailByProductIdOrderByPriceAsc(Integer productId);


    @Query(value = "SELECT pd FROM ProductDetail pd\n" +
            "INNER JOIN Product p ON pd.product.id = p.id\n" +
            "INNER JOIN Image i ON i.product.id = p.id\n" +
            "WHERE p.status IN (1,2) AND pd.status = 1 AND i.status = 1 AND p.id =:productId\n" +
            "ORDER BY pd.priceDiscount DESC")
    List<ProductDetail> getAllProductDetailByProductIdOrderByPriceDiscountDesc(Integer productId);

    @Query(value = "SELECT pd FROM ProductDetail pd\n" +
            "INNER JOIN Product p ON pd.product.id = p.id\n" +
            "INNER JOIN Image i ON i.product.id = p.id\n" +
            "WHERE p.status IN (1,2) AND pd.status = 1 AND i.status = 1 AND p.id =:productId\n" +
            "ORDER BY pd.priceDiscount ASC")
    List<ProductDetail> getAllProductDetailByProductIdOrderByPriceDiscountAsc(Integer productId);

    @Query(value = "SELECT pd.id as id, " +
            " pd.code as code, " +
            " pd.quantity as quantity," +
            " pd.price as price," +
            " pd.priceDiscount as priceDiscount," +
            " s.name as sizeName," +
            " c.name as colorName" +
            " FROM ProductDetail pd \n" +
            "INNER JOIN Size s ON s.id = pd.size.id \n " +
            "INNER JOIN Color c ON c.id = pd.color.id \n " +
            "INNER JOIN Product p ON p.id = pd.product.id \n" +
            "WHERE p.id =:productId \n" +
            "AND (pd.priceDiscount >= :priceMinDiscount AND pd.priceDiscount <= :priceMaxDiscount)" +
            "AND ( s.id =:sizeId OR :sizeId IS NULL )\n" +
            "AND ( c.id =:colorId OR :colorId IS NULL)\n" +
            "AND pd.status = 1\n " +
            "ORDER BY pd.updateDate DESC")
    List<ProductDetailDto> getProductDetailByPriceAndSizeIdAndColorId(Integer productId, BigDecimal priceMinDiscount, BigDecimal priceMaxDiscount, Integer sizeId, Integer colorId);


    @Query(value = "SELECT pd.id as id," +
            " pd.quantity as quantity," +
            " pd.price as price," +
            " s.name as sizeName," +
            " c.name as colorName" +
            " FROM ProductDetail pd \n" +
            "INNER JOIN Size s ON s.id = pd.size.id \n " +
            "INNER JOIN Color c ON c.id = pd.color.id \n " +
            "INNER JOIN Product p ON p.id = pd.product.id \n" +
            "WHERE p.id =:productId \n" +
            "AND (pd.price >= :priceMin AND pd.price <= :priceMax)" +
            "AND ( s.id =:sizeId OR :sizeId IS NULL )\n" +
            "AND ( c.id =:colorId OR :colorId IS NULL)\n" +
            "AND pd.status = 1\n " +
            "ORDER BY pd.updateDate DESC")
    List<ProductDetailDto> getProductByPriceAndSizeIdAndColorId(Integer productId, BigDecimal priceMin, BigDecimal priceMax, Integer sizeId, Integer colorId);


    @Query(value = "SELECT pd.id as id, " +
            " pd.code as code, " +
            " pd.quantity as quantity," +
            " pd.price as price," +
            " s.name as sizeName," +
            " c.name as colorName" +
            " FROM ProductDetail pd \n" +
            "INNER JOIN Size s ON s.id = pd.size.id \n " +
            "INNER JOIN Color c ON c.id = pd.color.id \n " +
            "INNER JOIN Product p ON p.id = pd.product.id \n" +
            "WHERE p.id =:productId \n" +
            "AND (pd.price >= :priceMin AND pd.price <= :priceMax)" +
            "AND ( s.id =:sizeId OR :sizeId IS NULL )\n" +
            "AND ( c.id =:colorId OR :colorId IS NULL)\n" +
            "AND pd.status = 0\n " +
            "ORDER BY pd.updateDate DESC")
    Page<ProductDetailDto> getProductByPriceAndSizeIdAndColorIdDeleted(Integer productId, BigDecimal priceMin, BigDecimal priceMax, Integer sizeId, Integer colorId, Pageable pageable);


    @Query(value = "SELECT pd.id as id," +
            " pd.quantity as quantity," +
            " pd.price as price," +
            " s.name as sizeName," +
            " c.name as colorName" +
            " FROM ProductDetail pd \n" +
            "INNER JOIN Size s ON s.id = pd.size.id \n " +
            "INNER JOIN Color c ON c.id = pd.color.id \n " +
            "INNER JOIN Product p ON p.id = pd.product.id \n" +
            "WHERE p.id =:productId \n" +
            "AND (pd.price >= :priceMin AND pd.price <= :priceMax)" +
            "AND ( s.id =:sizeId OR :sizeId IS NULL )\n" +
            "AND ( c.id =:colorId OR :colorId IS NULL)\n" +
            "AND pd.status = 0\n " +
            "ORDER BY pd.updateDate DESC")
    List<ProductDetailDto> getProductByPriceAndSizeIdAndColorIdDeleted(Integer productId, BigDecimal priceMin, BigDecimal priceMax, Integer sizeId, Integer colorId);

    List<ProductDetail> findAllByStatusOrderByIdDesc(Integer status);

    @Query("select prd from ProductDetail prd where " +
            "prd.product.name like ?1 " +
            "and prd.color.code like ?2 " +
            "and prd.product.material.code like ?3 " +
            "and prd.size.code like ?4")
    List<ProductDetail> locSpTaiQuay(String s, String colorCode, String matrCode, String sizeName);
}
