package com.sd38.gymtiger.controller.admin;

import com.sd38.gymtiger.model.*;
import com.sd38.gymtiger.response.CreateProductDetails;
import com.sd38.gymtiger.response.ProductDetailSearchResponse;
import com.sd38.gymtiger.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/tiger")
public class ProductDetailController {
    @Autowired
    private ProductDetailService productDetailService;
    @Autowired
    private SizeService sizeService;

    @Autowired
    private ColorService colorService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/product/{productId}/product-detail")
    public String getProductDetail(@PathVariable Integer productId, Model model, @RequestParam(defaultValue = "0") int page) {
        List<Size> listSize = sizeService.getAll();
        List<Color> listColor = colorService.getAll();
        var pageProductDetail = productDetailService.getProductByPriceAndSizeIdAndColorId(page, productId, BigDecimal.valueOf(0), BigDecimal.valueOf(10000000), null, null);
//                .stream().map(ProductDetailDtoImpl::toProductSearchResponse).collect(Collectors.toList());
        model.addAttribute("pageProductDetail", pageProductDetail);
        List<Image> listImage = imageService.getAllImageByProductId(productId);
        model.addAttribute("listImage", listImage);
        Product product = productService.getOne(productId);
        BigInteger totalQuantity = productDetailService.getTotalQuantityByProductId(productId);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("product", product);
        model.addAttribute("listSize", listSize);
        model.addAttribute("listColor", listColor);
        return "admin/product-detail/product-detail";
    }

//    @GetMapping("/product-detail/view-add/{productId}")
//    public String viewAdd(Model model, @PathVariable Integer productId) {
//        List<Size> listSize = sizeService.getAll();
//        List<Color> listColor = colorService.getAll();
//        Product product = productService.getOne(productId);
//        ProductDetail productDetail = new ProductDetail();
//        model.addAttribute("product", product);
//        model.addAttribute("productDetail", productDetail);
//        model.addAttribute("listSize", listSize);
//        model.addAttribute("listColor", listColor);
//        return "admin/product-detail/product-detail-add";
//    }

    @GetMapping("/product-detail/view-add")
    public String viewAdd(Model model, HttpSession httpSession) {
        List<Size> listSize = sizeService.getAll();
        List<Color> listColor = colorService.getAll();
        String randomKey = (String) httpSession.getAttribute("randomKey");
        Product productDataAdd = (Product) httpSession.getAttribute("productAdd" + randomKey);
        if (productDataAdd == null) {
            return "redirect:/tiger/product/view-add";
        }
        CreateProductDetails createProductDetails = new CreateProductDetails();
        List<ProductDetail> listProductDetail = new ArrayList<>();
        listProductDetail.add(new ProductDetail());
        createProductDetails.setListProductDetail(listProductDetail);
        model.addAttribute("createProductDetailForm", createProductDetails);
        model.addAttribute("listSize", listSize);
        model.addAttribute("listColor", listColor);
        return "admin/product-detail/product-detail-add";
    }


    @PostMapping("/product-detail/add")
    public String addProductFinal(@ModelAttribute CreateProductDetails createProductDetails,
                                  HttpSession httpSession,
                                  @RequestParam("files") List<MultipartFile> files,
                                  RedirectAttributes redirectAttributes) throws IOException {
        String randomKey = (String) httpSession.getAttribute("randomKey");
        Product productAdd = (Product) httpSession.getAttribute("productAdd" + randomKey);
        if (productAdd == null) {
            return "redirect:/tiger/product/view-add";
        } else {
            List<ProductDetail> productDetailList = createProductDetails.getListProductDetail();
            productService.addFinal(productAdd, productDetailList, files);
        }
        httpSession.removeAttribute("randomKey");
        httpSession.removeAttribute("productAdd" + randomKey);
        redirectAttributes.addFlashAttribute("mess", "Thêm dữ liệu thành công");
        return "redirect:/tiger/product/page";
    }


    @GetMapping("/product/{productId}/product-detail/view-update")
    public String viewUpdate(Model model,
                             HttpSession httpSession,
                             @PathVariable Integer productId) {
        List<Size> listSize = sizeService.getAll();
        List<Color> listColor = colorService.getAll();
        String randomKey = (String) httpSession.getAttribute("randomKey");
        Product productDataUpdate = (Product) httpSession.getAttribute("productUpdate" + randomKey);
        if (productDataUpdate == null) {
            return "redirect:/tiger/product/view-update/" + productId;
        }
        Product product = productService.getOne(productId);
        httpSession.setAttribute("productBefore", product);
        List<ProductDetail> listProductDetailNoDeleteResponse = productDetailService.getProductDetailNoDeleteResponse(product.getListProductDetail());
        CreateProductDetails createProductDetails = new CreateProductDetails();
//        createProductDetails.setListProductDetail(product.getListProductDetail());
        createProductDetails.setListProductDetail(listProductDetailNoDeleteResponse);
        model.addAttribute("createProductDetailForm", createProductDetails);
        model.addAttribute("listImage", imageService.getImageResponse(product.getListImage()));
        model.addAttribute("numberImage", imageService.getImageResponse(product.getListImage()).size());
        model.addAttribute("listSize", listSize);
        model.addAttribute("listColor", listColor);
        return "admin/product-detail/product-detail-update";
    }

    @PostMapping("/product-detail/update")
    public String updateProductFinal(@ModelAttribute CreateProductDetails createProductDetails,
                                     HttpSession httpSession,
                                     @RequestParam(value = "imageRemoveIds", required = false) List<Integer> imageRemoveIds,
                                     @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                     @RequestParam(value = "productDetailRemoveIds", required = false) List<Integer> productDetailRemoveIds,
                                     RedirectAttributes redirectAttributes) throws IOException {
        String randomKey = (String) httpSession.getAttribute("randomKey");
        Product productUpdate = (Product) httpSession.getAttribute("productUpdate" + randomKey);
        Product productBefore = (Product) httpSession.getAttribute("productBefore");
        if (productUpdate == null) {
            return "redirect:/tiger/product/" + productUpdate.getId() + "/view-update";
        } else {
            List<ProductDetail> productDetailList = createProductDetails.getListProductDetail();
            productService.updateFinal(productBefore, productUpdate, productDetailList, files, imageRemoveIds, productDetailRemoveIds);
        }
        httpSession.removeAttribute("randomKey");
        httpSession.removeAttribute("productAdd" + randomKey);
        redirectAttributes.addFlashAttribute("mess", "Thêm dữ liệu thành công");
        return "redirect:/tiger/product/page";
    }



    @PostMapping("/product-detail/add/one")
    public String add(@RequestParam Integer productId,
                      @RequestParam Integer quantity,
                      @RequestParam BigDecimal price,
                      @RequestParam Integer sizeId,
                      @RequestParam Integer colorId,
                      RedirectAttributes redirectAttributes) throws IOException {
        if (productDetailService.add(productId, quantity, price, sizeId, colorId)) {
            redirectAttributes.addFlashAttribute("mess", "Thêm dữ liệu thành công");
            return "redirect:/tiger/product/" + productId + "/product-detail";
        } else {
            redirectAttributes.addFlashAttribute("error", "Mã chi tiết đã tồn tại, Thêm dữ liệu thất bại");
            return "redirect:/tiger/product/" + productId + "/product-detail";
        }
    }

    @PostMapping("/product-detail/excel")
    public String importExcel(@RequestParam Integer productId,
                              RedirectAttributes redirectAttributes,
                              @RequestParam MultipartFile excelFile) throws IOException {
        if (productDetailService.importExcelProduct(excelFile, productId).contains("Oke bạn ơi")) {
            redirectAttributes.addFlashAttribute("mess", "Thêm dữ liệu excel thành công");
            return "redirect:/tiger/product/" + productId + "/product-detail";
        } else if (productDetailService.importExcelProduct(excelFile, productId).contains("Sai dữ liệu")) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng kiểm tra lại dữ liệu trong file");
            return "redirect:/tiger/product/" + productId + "/product-detail";
        } else {
            redirectAttributes.addFlashAttribute("error", "Đây không phải là file excel");
            return "redirect:/tiger/product/" + productId + "/product-detail";
        }
    }


    @GetMapping("/product-detail/update-success")
    public String updateSuccess(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mess", "Cập nhật dữ liệu thành công");
        return "redirect:/tiger/product/page";
    }

    @GetMapping("/product-detail/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        ProductDetail productDetail = productDetailService.getOne(id);
        model.addAttribute("productDetail", productDetail);
        List<Size> listSize = sizeService.getAll();
        List<Color> listColor = colorService.getAll();
        model.addAttribute("listSize", listSize);
        model.addAttribute("listColor", listColor);
        return "admin/product-detail/product-detail-update-one";
    }

    @PostMapping("/product-detail/update-one/{id}")
    public String update(@PathVariable Integer id,
                         @RequestParam Integer productId,
                         @RequestParam Integer quantity,
                         @RequestParam BigDecimal price,
                         @RequestParam Integer sizeId,
                         @RequestParam Integer colorId,
                         RedirectAttributes redirectAttributes) {
        productDetailService.update(id, productId, quantity, price, sizeId, colorId);
        redirectAttributes.addFlashAttribute("mess", "Sửa dữ liệu thành công");
        return "redirect:/tiger/product/" + productId + "/product-detail";
    }

    @PostMapping("/product-detail/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        ProductDetail productDetail = productDetailService.getOne(id);
        productDetailService.delete(id);
        redirectAttributes.addFlashAttribute("mess", "Xóa dữ liệu thành công");
        return "redirect:/tiger/product/" + productDetail.getProduct().getId() + "/product-detail";
    }

    @GetMapping("/product/{productId}/product-detail/search")
    public String search(@PathVariable Integer productId,
                         Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(required = false) BigDecimal priceMinDiscount,
                         @RequestParam(required = false) BigDecimal priceMaxDiscount,
                         @RequestParam(required = false) Integer sizeId,
                         @RequestParam(required = false) Integer colorId) {

        if (priceMinDiscount == null && priceMaxDiscount == null && sizeId == null && colorId == null) {
            return "redirect:/tiger/product/" + productId + "/product-detail";
        }

        if (priceMinDiscount == null) {
            priceMinDiscount = BigDecimal.valueOf(0);
        }
        if (priceMaxDiscount == null) {
            priceMaxDiscount = BigDecimal.valueOf(Integer.MAX_VALUE);
        }
        List<Size> listSize = sizeService.getAll();
        List<Color> listColor = colorService.getAll();

        Page<ProductDetailSearchResponse> productDetailSearchResponses = productDetailService.getProductByPriceAndSizeIdAndColorId(page, productId, priceMinDiscount, priceMaxDiscount, sizeId, colorId);
//        int totalPage = productDetailService.getTotalPage(page, productId, priceMinDiscount, priceMaxDiscount, sizeId, colorId);
        model.addAttribute("pageProductDetail", productDetailSearchResponses);
        List<Image> listImage = imageService.getAllImageByProductId(productId);
        BigInteger totalQuantity = productDetailService.getTotalQuantityByProductId(productId);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("listImage", listImage);
//        model.addAttribute("totalPage", totalPage);
        Product product = productService.getOne(productId);
        model.addAttribute("productId", productId);
        model.addAttribute("product", product);
        model.addAttribute("listSize", listSize);
        model.addAttribute("listColor", listColor);
        model.addAttribute("sizeId", sizeId);
        model.addAttribute("colorId", colorId);
        model.addAttribute("priceMinDiscount", priceMinDiscount);
        model.addAttribute("priceMaxDiscount", priceMaxDiscount);
        return "admin/product-detail/product-detail";
    }

    @GetMapping("/product/{productId}/product-detail/view-restore")
    public String viewRestore(@PathVariable Integer productId,
                              @RequestParam(defaultValue = "0") int page, Model model) {
        List<Size> listSize = sizeService.getAll();
        List<Color> listColor = colorService.getAll();
        var pageProductDetail = productDetailService.getProductByPriceAndSizeIdAndColorIdDeleted(page, productId, BigDecimal.valueOf(0), BigDecimal.valueOf(Integer.MAX_VALUE), null, null);
        model.addAttribute("pageProductDetail", pageProductDetail);
        Product product = productService.getOne(productId);
        model.addAttribute("productId", productId);
        model.addAttribute("product", product);
        model.addAttribute("listSize", listSize);
        model.addAttribute("listColor", listColor);
        return "admin/product-detail/product-detail-restore";
    }

    @PostMapping("/product/{productId}/product-detail/restore/{id}")
    public String restore(@PathVariable Integer productId,
                          @PathVariable Integer id,
                          RedirectAttributes redirectAttributes) {
        productDetailService.restore(id);
        redirectAttributes.addFlashAttribute("mess", "Khôi phục dữ liệu thành công");
        return "redirect:/tiger/product/" + productId + "/product-detail/view-restore";
    }

    @GetMapping("/product/{productId}/product-detail/search-restore")
    public String searchDeleted(@PathVariable Integer productId,
                                Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) BigDecimal priceMin,
                                @RequestParam(required = false) BigDecimal priceMax,
                                @RequestParam(required = false) Integer sizeId,
                                @RequestParam(required = false) Integer colorId) {
        if (priceMin == null) {
            priceMin = BigDecimal.valueOf(0);
//            model.addAttribute("priceMinNull", null);
        }
        if (priceMax == null) {
            priceMax = BigDecimal.valueOf(Integer.MAX_VALUE);
//            model.addAttribute("priceMaxNull", null);
        }

        List<Size> listSize = sizeService.getAll();
        List<Color> listColor = colorService.getAll();

        Page<ProductDetailSearchResponse> productDetailSearchResponses = productDetailService.getProductByPriceAndSizeIdAndColorIdDeleted(page, productId, priceMin, priceMax, sizeId, colorId);
        int totalPage = productDetailService.getTotalPageDeleted(page, productId, priceMin, priceMax, sizeId, colorId);
        model.addAttribute("pageProductDetail", productDetailSearchResponses);
        model.addAttribute("totalPage", totalPage);
        Product product = productService.getOne(productId);
        model.addAttribute("productId", productId);
        model.addAttribute("product", product);
        model.addAttribute("listSize", listSize);
        model.addAttribute("listColor", listColor);
        model.addAttribute("sizeId", sizeId);
        model.addAttribute("colorId", colorId);
        model.addAttribute("priceMin", priceMin);
        model.addAttribute("priceMax", priceMax);
        return "admin/product-detail/product-detail-restore";
    }
}
