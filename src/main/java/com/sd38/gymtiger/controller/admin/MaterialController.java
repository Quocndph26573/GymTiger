package com.sd38.gymtiger.controller.admin;

import com.sd38.gymtiger.model.Material;
import com.sd38.gymtiger.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;


@Controller
@RequestMapping("/tiger/material")
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    @GetMapping("/page")
    public String getPage(@RequestParam(defaultValue = "0") Integer page, Model model) {
        Page<Material> pageMaterial = materialService.getPage(page);
        model.addAttribute("pageMaterial", pageMaterial);
        model.addAttribute("page", page);
        return "admin/material/material";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Material material = materialService.detail(id);
        model.addAttribute("material", material);
        return "/admin/material/material-detail";
    }

    @PostMapping("/add")
    public String add(@RequestParam String name,
                      RedirectAttributes redirectAttributes) {
        if (materialService.add(name)) {
            redirectAttributes.addFlashAttribute("mess", "Thêm dữ liệu thành công");
            return "redirect:/tiger/material/page";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tên chất liệu đã tồn tại");
            return "redirect:/tiger/material/page";
        }
    }

    @PostMapping("/attribute")
    public String attribute(@RequestParam String materialName,
                            RedirectAttributes redirectAttributes) {
        if (materialService.add(materialName)) {
            redirectAttributes.addFlashAttribute("mess", "Thêm dữ liệu thành công");
            return "redirect:/tiger/product/view-add";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tên chất liệu đã tồn tại");
            return "redirect:/tiger/product/view-add";
        }
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Material material,
                         RedirectAttributes redirectAttributes) {
        if (materialService.update(material, id)) {
            redirectAttributes.addFlashAttribute("mess", "Sửa dữ liệu thành công");
            return "redirect:/tiger/material/page";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tên chất liệu đã tồn tại");
            return "redirect:/tiger/material/detail/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        materialService.delete(id);
        redirectAttributes.addFlashAttribute("mess", "Xóa dữ liệu thành công");
        return "redirect:/tiger/material/page";
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam(required = false) String materialNameSearch,
                         @RequestParam(defaultValue = "0") int page) {
        Page<Material> pageMaterial = materialService.search(materialNameSearch, page);
        if ("".equals(materialNameSearch) || materialNameSearch.isEmpty()) {
            return "redirect:/tiger/material/page";
        }
        model.addAttribute("materialNameSearch", materialNameSearch);
        model.addAttribute("pageMaterial", pageMaterial);
        return "admin/material/material";
    }

    @GetMapping("/view-restore")
    public String viewRestore(@RequestParam(defaultValue = "0") Integer page, Model model) {
        Page<Material> pageMaterial = materialService.getAllDeleted(page);
        model.addAttribute("pageMaterial", pageMaterial);
        return "admin/material/material-restore";
    }

    @PostMapping("/restore/{id}")
    public String restore(@PathVariable Integer id,
                          RedirectAttributes redirectAttributes) {
        materialService.restore(id);
        redirectAttributes.addFlashAttribute("mess", "Khôi phục dữ liệu thành công");
        return "redirect:/tiger/material/view-restore";
    }

    @GetMapping("/search-restore")
    public String searchDelete(Model model, @RequestParam(required = false) String materialNameSearch,
                               @RequestParam(defaultValue = "0") int page) {
        Page<Material> pageMaterial = materialService.searchDelete(materialNameSearch, page);
        if ("".equals(materialNameSearch) || materialNameSearch.isEmpty()) {
            return "redirect:/tiger/material/view-restore";
        }
        model.addAttribute("materialNameSearch", materialNameSearch);
        model.addAttribute("pageMaterial", pageMaterial);
        return "admin/material/material-restore";
    }

    @PostMapping("/excel")
    public String importExcel(RedirectAttributes redirectAttributes, @RequestParam MultipartFile excelFile) throws IOException {
        String result = materialService.importExcel(excelFile);
        if (result.contains("Oke")) {
            redirectAttributes.addFlashAttribute("mess", "Thêm dữ liệu excel thành công");
            return "redirect:/tiger/material/page";
        } else if (result.contains("Sai dữ liệu")) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng kiểm tra lại kiểu dữ liệu trong file");
            return "redirect:/tiger/material/page";
        } else if (result.contains("Tồn tại")) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu trong file đã tồn tại");
            return "redirect:/tiger/material/page";
        } else if (result.contains("Trống")) {
            redirectAttributes.addFlashAttribute("error", "Trong file excel không có dữ liệu");
            return "redirect:/tiger/brand/page";
        } else if (result.contains("Trùng")) {
            redirectAttributes.addFlashAttribute("error", "1 số dữ liệu trong file bị trùng lặp");
            return "redirect:/tiger/material/page";
        } else {
            redirectAttributes.addFlashAttribute("error", "Đây không phải là file excel");
            return "redirect:/tiger/material/page";
        }
    }
}
