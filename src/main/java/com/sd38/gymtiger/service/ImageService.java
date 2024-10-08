package com.sd38.gymtiger.service;

import com.sd38.gymtiger.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    List<Image> getAll();

    List<Image> getAllImageByProductIdNoStatus(Integer productId);

    Page<Image> getImageByProduct(int page, Integer productId);

    void add(Integer productId, List<MultipartFile> images);

    void update(Integer id, Integer productId, MultipartFile image);

    Integer getProductDetailByIdImage(Integer imageId);

    Image delete(Integer id);

    byte[] getImage(Integer imageId);

    byte[] getImageDeleted(Integer imageId);

    Image detail(Integer id);

    byte[] getImageByProductId(Integer productId);

    byte[] getImageDeletedByProductId(Integer productId);

    Page<Image> getAllDeletedByProductId(Integer productId, int page);

    Image restore(Integer id);

    List<Image> getAllImageByProductId(Integer productId);

    Image getImageActiveByProductId(Integer productId);

    List<Image> getImageResponse(List<Image> listImage);

}
