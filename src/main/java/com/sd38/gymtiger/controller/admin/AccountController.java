package com.sd38.gymtiger.controller.admin;

import com.sd38.gymtiger.model.Account;
import com.sd38.gymtiger.service.AccountService;
import com.sd38.gymtiger.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@RequestMapping("/tiger/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RoleService roleService;


    @GetMapping("/page")
    public String getPage(@RequestParam(defaultValue = "0", value = "page") Integer page, Model model) {
        Page<Account> pageAccount = accountService.getPage(page);
        model.addAttribute("pageAccount", pageAccount);
        model.addAttribute("listRole", roleService.getAll());
//        System.out.println(SecurityUtil.getSessionUser());
        return "admin/account/account";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Account account = accountService.findOne(id);
        model.addAttribute("account", account);
        model.addAttribute("listRole", roleService.getAll());
        return "/admin/account/account-detail";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mess", "Xoá dữ liệu thành công");
        accountService.delete(id);
        return "redirect:/tiger/account/page";
    }


    @PostMapping("/add")
    public String add(@ModelAttribute("Account") Account account,
                      @RequestParam("avt") MultipartFile avt,
                      @RequestParam("roleId") Integer roleId,
                      RedirectAttributes redirectAttributes) {
        if (accountService.add(account, avt, roleId).contains("ok")) {
            redirectAttributes.addFlashAttribute("mess", "Thêm dữ liệu thành công");
            return "redirect:/tiger/account/page";
        } else if (accountService.add(account, avt, roleId).contains("Email")) {
            redirectAttributes.addFlashAttribute("error", "Email đã tồn tại");
            return "redirect:/tiger/account/page";
        } else {
            redirectAttributes.addFlashAttribute("error", "Số điện thoại đã tồn tại");
            return "redirect:/tiger/account/page";
        }
    }

    @PostMapping("/update/{id}")
    public String update(@ModelAttribute("Account") Account account,
                         @RequestParam(value = "avt", required = false) MultipartFile avt,
                         @RequestParam("roleId") Integer roleId,
                         @PathVariable Integer id,
                         RedirectAttributes redirectAttributes) {
        if (accountService.update(account, avt, roleId, id).contains("ok")) {
            redirectAttributes.addFlashAttribute("mess", "Sửa dữ liệu thành công");
            return "redirect:/tiger/account/page";
        } else if (accountService.update(account, avt, roleId, id).contains("Email")) {
            redirectAttributes.addFlashAttribute("error", "Email đã tồn tại");
            return "redirect:/tiger/account/detail" + id;
        } else {
            redirectAttributes.addFlashAttribute("error", "Số điện thoại đã tồn tại");
            return "redirect:/tiger/account/detail" + id;
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String name,
                         @RequestParam(required = false) String birthday,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String phoneNumber,
                         @RequestParam(required = false) Integer roleId,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) throws ParseException {
        if (name.isEmpty() && birthday.isEmpty() && email.isEmpty() && phoneNumber.isEmpty() && roleId == null) {
            return "redirect:/tiger/account/page";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthdayDate = null;
        if (birthday.isEmpty()) {
            birthday = null;
        } else {
            birthdayDate = sdf.parse(birthday);
        }
        Page<Account> pageAccount = accountService.search(name, birthdayDate, email, phoneNumber, roleId, page);
        model.addAttribute("pageAccount", pageAccount);
        model.addAttribute("name", name);
        model.addAttribute("birthday", birthday);
        model.addAttribute("email", email);
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("roleId", roleId);
        model.addAttribute("listRole", roleService.getAll());
        return "admin/account/account";
    }
}
