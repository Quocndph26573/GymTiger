package com.sd38.gymtiger.controller.admin;

import com.sd38.gymtiger.model.Category;
import com.sd38.gymtiger.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;


@Controller
@RequestMapping("/tiger/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public String getPage(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Category> pageCategory = categoryService.getPage(page);
        model.addAttribute("pageCategory", pageCategory);
        model.addAttribute("page", page);
        return "admin/category/category";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Category category = categoryService.detail(id);
        model.addAttribute("category", category);
        return "/admin/category/category-detail";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("mess", "Xóa dữ liệu thành công");
        return "redirect:/tiger/category/page";
    }


    @PostMapping("/add")
    public String add(@RequestParam String name,
                      RedirectAttributes redirectAttributes) {
        if (categoryService.add(name)) {
            redirectAttributes.addFlashAttribute("mess", "Thêm dữ liệu thành công");
            return "redirect:/tiger/category/page";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tên loại sản phẩm đã tồn tại");
            return "redirect:/tiger/category/page";
        }
    }

    @PostMapping("/attribute")
    public String attribute(@RequestParam String categoryName,
                            RedirectAttributes redirectAttributes) {
        if (categoryService.add(categoryName)) {
            redirectAttributes.addFlashAttribute("mess", "Thêm dữ liệu thành công");
            return "redirect:/tiger/product/view-add";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tên loại sản phẩm đã tồn tại");
            return "redirect:/tiger/product/view-add";
        }
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Category category,
                         RedirectAttributes redirectAttributes) {
        if (categoryService.update(category, id)) {
            redirectAttributes.addFlashAttribute("mess", "Sửa dữ liệu thành công");
            return "redirect:/tiger/category/page";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tên loại đã tồn tại");
            return "redirect:/tiger/category/detail/" + id;
        }
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam(required = false) String categoryNameSearch,
                         @RequestParam(defaultValue = "0") int page) {
        Page<Category> pageCategory = categoryService.search(categoryNameSearch, page);
        if ("".equals(categoryNameSearch) || categoryNameSearch.isEmpty()) {
            return "redirect:/tiger/category/page";
        }
        model.addAttribute("categoryNameSearch", categoryNameSearch);
        model.addAttribute("pageCategory", pageCategory);
        return "admin/category/category";
    }

    @GetMapping("/view-restore")
    public String viewRestore(@RequestParam(defaultValue = "0") Integer page, Model model) {
        Page<Category> pageCategory = categoryService.getAllDeleted(page);
        model.addAttribute("pageCategory", pageCategory);
        return "admin/category/category-restore";
    }

    @PostMapping("/restore/{id}")
    public String restore(@PathVariable Integer id,
                          RedirectAttributes redirectAttributes) {
        categoryService.restore(id);
        redirectAttributes.addFlashAttribute("mess", "Khôi phục dữ liệu thành công");
        return "redirect:/tiger/category/view-restore";
    }

    @GetMapping("/search-restore")
    public String searchDelete(Model model, @RequestParam(required = false) String categoryNameSearch,
                               @RequestParam(defaultValue = "0") int page) {
        Page<Category> pageCategory = categoryService.searchDelete(categoryNameSearch, page);
        if ("".equals(categoryNameSearch) || categoryNameSearch.isEmpty()) {
            return "redirect:/tiger/category/view-restore";
        }
        model.addAttribute("categoryNameSearch", categoryNameSearch);
        model.addAttribute("pageCategory", pageCategory);
        return "admin/category/category-restore";
    }

    @PostMapping("/excel")
    public String importExcel(RedirectAttributes redirectAttributes, @RequestParam MultipartFile excelFile) throws IOException {
        String result = categoryService.importExcel(excelFile);
        if (result.contains("Oke")) {
            redirectAttributes.addFlashAttribute("mess", "Thêm dữ liệu excel thành công");
            return "redirect:/tiger/category/page";
        } else if (result.contains("Sai dữ liệu")) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng kiểm tra lại kiểu dữ liệu trong file");
            return "redirect:/tiger/category/page";
        } else if (result.contains("Tồn tại")) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu trong file đã tồn tại");
            return "redirect:/tiger/category/page";
        } else if (result.contains("Trống")) {
            redirectAttributes.addFlashAttribute("error", "Trong file excel không có dữ liệu");
            return "redirect:/tiger/brand/page";
        } else if (result.contains("Trùng")) {
            redirectAttributes.addFlashAttribute("error", "1 số dữ liệu trong file bị trùng lặp");
            return "redirect:/tiger/category/page";
        } else {
            redirectAttributes.addFlashAttribute("error", "Đây không phải là file excel");
            return "redirect:/tiger/category/page";
        }
    }
}
