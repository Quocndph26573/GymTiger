package com.sd38.gymtiger.repository;

import com.sd38.gymtiger.dto.admin.thongke.TKSanPham;
import com.sd38.gymtiger.model.BillDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongkeSanPhamRepository extends JpaRepository<BillDetail, Integer> {
    @Query(value = "Select  top 5 pro.Id as 'Id' ,p.Code as 'Code', pro.Name as 'Name', SUM(bi.Quantity) as 'SoLuong', SUM(bi.Quantity * bi.Price) as 'DoanhThu',si.Name as 'Size',co.Name as 'Color' from BillDetail bi\n" +
            "            join Bill b on b.Id = bi.BillId\n" +
            "            join ProductDetail p on p.Id = bi.ProductDetailId\n" +
            "            join Product pro on pro.Id = p.ProductId\n" +
            "\t\t\tjoin Size si on si.Id = p.SizeId\n" +
            "\t\t\tjoin Color co on co.Id = p.ColorId\n" +
            "            WHERE b.Status = 1 \n" +
            "            Group by pro.Id  ,p.Code, pro.Name,si.Name,co.Name\n" +
            "            order by SUM(bi.Quantity) desc", nativeQuery = true)
    public List<TKSanPham> getTKSanPham();
}
